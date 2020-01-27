package com.codingwithmitch.espressodaggerexamples.ui

import androidx.lifecycle.*
import com.codingwithmitch.espressodaggerexamples.models.BlogPost
import com.codingwithmitch.espressodaggerexamples.models.Category
import com.codingwithmitch.espressodaggerexamples.repository.MainRepository
import com.codingwithmitch.espressodaggerexamples.ui.state.MainStateEvent
import com.codingwithmitch.espressodaggerexamples.ui.state.MainStateEvent.*
import com.codingwithmitch.espressodaggerexamples.ui.state.MainViewState
import com.codingwithmitch.espressodaggerexamples.util.DataState
import com.codingwithmitch.espressodaggerexamples.util.printLogD
import kotlinx.coroutines.cancel
import javax.inject.Inject

class MainViewModel
@Inject
constructor(
    val mainRepository: MainRepository
) :ViewModel(){

    private val CLASS_NAME = "MainViewModel"

    private val _categories: MutableLiveData<DataState<List<Category>>> = MutableLiveData()
    private val _blogs: MutableLiveData<DataState<List<BlogPost>>> = MutableLiveData()
    private val _selectedBlog: MutableLiveData<BlogPost> = MutableLiveData()

    val blogs: LiveData<DataState<List<BlogPost>>>
        get() = _blogs

    val selectedBlog: LiveData<BlogPost>
        get() = _selectedBlog

    val categories: LiveData<DataState<List<Category>>>
        get() = _categories

    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()
    private val _stateEvent: MutableLiveData<MainStateEvent> = MutableLiveData()

    val viewState: LiveData<MainViewState>
        get() = _viewState

    val stateEvent: LiveData<MainStateEvent>
        get() = _stateEvent

    val dataState: LiveData<DataState<Any>> = Transformations
        .switchMap(_stateEvent){stateEvent ->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }

    fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<Any>> {

        return when (stateEvent) {

            is GetAllBlogs -> {
                launchLiveDataJob{mainRepository.getAllBlogs()}
            }

            is GetCategories -> {
                launchLiveDataJob{mainRepository.getCategories()}
            }

            is SearchBlogsByCategory -> {
                launchLiveDataJob { mainRepository.getBlogs(stateEvent.category) }
            }
        }

    }

    fun setStateEvent(stateEvent: MainStateEvent){
        _stateEvent.value = stateEvent
    }

    fun setViewState(viewState: MainViewState){
        _viewState.value = viewState
    }

    fun getBlogPosts(category: String){
        launchJob(_blogs){mainRepository.getBlogs(category)}
    }

    fun getAllBlogs(){
        launchJob(_blogs){mainRepository.getAllBlogs()}
    }

    fun getCategories(){
        launchJob(_categories){mainRepository.getCategories()}
    }

    fun setSelectedBlogPost(blogPost: BlogPost){
        _selectedBlog.value = blogPost
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun getCurrentViewStateOrNew(): MainViewState{
        val value = viewState.value?.let{
            it
        }?: MainViewState()
        return value
    }

    fun setBlogListData(blogList: List<BlogPost>){
        val update = getCurrentViewStateOrNew()
        update.listFragmentView.blogs = blogList
        setViewState(update)
    }

    fun setCategoriesData(categories: List<Category>){
        val update = getCurrentViewStateOrNew()
        update.listFragmentView.categories = categories
        setViewState(update)
    }


    fun handleDataEvent(data: Any){
        when(data){
            is List<*> -> {
                if(data.size > 0){
                    if(data.get(0) is BlogPost){
                        setBlogListData(data as List<BlogPost>)
                        setStateEvent(GetCategories())
                    }
                    else if(data.get(0) is Category){
                        setCategoriesData(data as List<Category>)
                    }
                }
            }

        }
    }
}



















