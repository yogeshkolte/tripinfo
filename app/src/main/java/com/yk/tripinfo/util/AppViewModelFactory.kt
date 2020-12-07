package com.yk.tripinfo.util

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yk.tripinfo.app.TripInfoApp
import java.lang.reflect.InvocationTargetException


open class AppViewModelFactory ( val mApplication: TripInfoApp) : ViewModelProvider.AndroidViewModelFactory(mApplication){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
            try {
                modelClass.getConstructor(TripInfoApp::class.java).newInstance(mApplication)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        } else super.create(modelClass)
    }

    companion object {
        private var sInstance: AppViewModelFactory? = null

        /**
         * Retrieve a singleton instance of AndroidViewModelFactory.
         *
         * @param application an application to pass in [AndroidViewModel]
         * @return A valid [AndroidViewModelFactory]
         */
        fun getInstance(application: TripInfoApp): AppViewModelFactory {
            if (sInstance == null) {
                sInstance = AppViewModelFactory(application)
            }
            return sInstance!!
        }
    }
}