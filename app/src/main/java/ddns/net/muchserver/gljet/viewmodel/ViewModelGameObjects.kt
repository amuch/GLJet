package ddns.net.muchserver.gljet.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelGameObjects: ViewModel() {
    val isMenuVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val setIsMenuVisible = { isVisible: Boolean -> isMenuVisible.value = isVisible }
}