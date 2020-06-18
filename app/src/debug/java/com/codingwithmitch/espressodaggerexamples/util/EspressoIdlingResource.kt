package com.codingwithmitch.espressodaggerexamples.util

import androidx.test.espresso.idling.CountingIdlingResource


object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow){
            countingIdlingResource.decrement()
        }
    }

    fun isClear(): Boolean{
        if (!countingIdlingResource.isIdleNow){
            decrement()
            return false
        }
        else {
            return true
        }
    }
}
