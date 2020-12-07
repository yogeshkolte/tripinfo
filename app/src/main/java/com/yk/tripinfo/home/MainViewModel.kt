package com.yk.tripinfo.home

import android.app.Application
import androidx.lifecycle.*

class MainViewModel (application: Application) : AndroidViewModel(application){
    var currentLocation = MutableLiveData<String>("UNKNOWN")


}