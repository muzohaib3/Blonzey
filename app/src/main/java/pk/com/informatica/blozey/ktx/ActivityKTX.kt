package pk.com.informatica.boschprofile.view.ktx

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

fun <T : AppCompatActivity> AppCompatActivity.gotoActivity(targetActivityClass: Class<T>) {
    val intent = Intent(this, targetActivityClass)
    startActivity(intent)
}

fun AppCompatActivity.toastAct(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
fun Fragment.toastFrag(info: String) {
    requireContext().toast(info)
}


fun View.click(it: (View) -> Unit) {
    this.setOnClickListener(it)
}

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProvider.NewInstanceFactory().create(viewModelClass)


fun <T : AppCompatActivity> Fragment.gotoActivityFromFragment(targetActivityClass: Class<T>) {
    val intent = Intent(requireActivity(), targetActivityClass)
    startActivity(intent)
}


fun AppCompatActivity.setTransparentStatusBarColor(color: Int, view:Int) {
    val window: Window = window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.setSystemBarsAppearance(
            0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, color)

    // Set a solid color to the bottom navigation bar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val navBar: NavigationBarView? = findViewById(view)
        navBar?.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    }
}

fun Fragment.setTransparentStatusBarColor(color: Int, view: Int) {
    val window: Window = requireActivity().window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.setSystemBarsAppearance(
            0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(requireContext(), color)

    // Set a solid color to the bottom navigation bar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val navBar: NavigationBarView? = requireActivity().findViewById(view)
        navBar?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
    }
}

fun Context.returnNoAdapter(): SpinnerAdapter {
    val noItem: List<String> = arrayListOf("No Record")
    return ArrayAdapter(this, R.layout.simple_list_item_1, noItem)
}

