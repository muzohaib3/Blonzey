package pk.com.informatica.boschprofile.view.ktx

import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import pk.com.informatica.blozey.R
import java.util.*


fun Activity.disableTouch() {
	window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
	                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun Activity.enableTouch() {
	window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun FragmentManager.getCurrentNavigationFragment() : Fragment? =
	primaryNavigationFragment?.childFragmentManager?.fragments?.first()


fun Context.toastLong(message : String)
{
	Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.isTablet() : Boolean =
	((resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)

fun Context.animateLayout(changeToLayout : Int, currentLayout : ConstraintLayout)
{
	val constraintSet = ConstraintSet()
	constraintSet.clone(this, changeToLayout)

	val transition = ChangeBounds()
	transition.interpolator = AnticipateOvershootInterpolator(1.0f)
	transition.duration = 1200

	TransitionManager.beginDelayedTransition(currentLayout, transition)
	constraintSet.applyTo(currentLayout) //here currentLayout is the name of view to which we are applying the constraintSet
}

fun Activity.getStatusBarHeight() : Int
{
	val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
	return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
	else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
}

fun Activity.getNavBarHeight() : Int
{
	var navigationBarHeight = 0
	val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
	if (resourceId > 0)
	{
		navigationBarHeight = resources.getDimensionPixelSize(resourceId)
	}

	return navigationBarHeight
}

fun Context.openActivity(T : Any)
{
	startActivity(Intent(this, T::class.java))
}

fun <T> Context.openActivityExtras(it : Class<T>, extras : Bundle.() -> Unit = {})
{
	val intent = Intent(this, it)
	intent.putExtras(Bundle().apply(extras))
	startActivity(intent)
}



fun ViewGroup.inflate(@LayoutRes layoutRes : Int, attachToRoot : Boolean = false) : View
{
	return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Activity.loadDefaults()
{
	//window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
	makeMeTransparent()
	makeLandscape()
	makeFullScreen()
}

fun Context.sbError(view : View, message : String, action : String = "Close", actionBlock : () -> Unit)
{
	val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction(action) { actionBlock() }
		.setActionTextColor(ContextCompat.getColor(this, R.color.white))

	val snackTextView =
		snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

	snackTextView.maxLines = 10
	snackbar.show()
}



fun Context.openAppSystemSettings() {
	startActivity(Intent().apply {
		action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
		data = Uri.fromParts("package", packageName, null)
	})
}

fun Activity.makePortrait()
{
	this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Activity.makeLandscape()
{
	this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Activity.makeMeTransparent()
{
	val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
	window.decorView.systemUiVisibility = uiOptions
}

fun Activity.makeFullScreen()
{
	window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

	window.decorView.systemUiVisibility =
		View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
}

fun Context.isGPSEnabled() : Boolean
{
	val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

	return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Activity.getScreenHeight() : Int
{
	val size = Point()
	windowManager.defaultDisplay.getSize(size)
	return size.y
}

fun Context.openPlayStore()
{
	try
	{
		startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
	}
	catch (e : ActivityNotFoundException)
	{
		startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
            )
        )
	}
}

/** dp size to px size. */
internal fun Context.dp2Px(dp : Int) : Int
{
	val scale = resources.displayMetrics.density
	return (dp * scale).toInt()
}

/** dp size to px size. */
internal fun Context.dp2Px(dp : Float) : Float
{
	val scale = resources.displayMetrics.density
	return (dp * scale)
}

/** gets a drawable from the resource. */
internal fun Context.contextDrawable(resource : Int) : Drawable?
{
	return ContextCompat.getDrawable(this, resource)
}

inline fun Context.notification(channelId : String, func : NotificationCompat.Builder.() -> Unit
) : Notification
{
	val builder = NotificationCompat.Builder(this, channelId)
	builder.func()
	return builder.build()
}

fun Context.sendLocalBroadcast(intent : Intent)
{
	LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}

fun Context.sendLocalBroadcastSync(intent : Intent)
{
	LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent)
}

fun Context.isServiceRunning(serviceClass : Class<*>) : Boolean
{
	val className = serviceClass.name
	val manager = activityManager
	return manager.getRunningServices(Integer.MAX_VALUE).any { className == it.service.className }
}

val Context.notificationManager : NotificationManager
	get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

val Context.connectivityManager : ConnectivityManager
	get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.powerManager : PowerManager
	get() = getSystemService(Context.POWER_SERVICE) as PowerManager

val Context.clipboardManager : ClipboardManager
	get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

val Context.activityManager : ActivityManager
	get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

val Context.layoutInflater : LayoutInflater
	get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

val Context.inputMethodManager : InputMethodManager
	get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

/**
 * Extension method to dial telephone number for Context.
 */
fun Context.dialNumber(tel : String?) = startActivity(
    Intent(
        Intent.ACTION_DIAL, Uri.parse("tel:" + tel)
    )
)

/**
 * Extension method to send sms for Context.
 */
fun Context.sendSMS(phone : String?, body : String = "")
{
	val smsToUri = Uri.parse("smsto:$phone")
	val intent = Intent(Intent.ACTION_SENDTO, smsToUri)
	intent.putExtra("sms_body", body)
	startActivity(intent)
}

fun PackageManager.isAppInstalled(packageName : String) : Boolean = try
{
	getApplicationInfo(packageName, PackageManager.GET_META_DATA)
	true
}
catch (e : Exception)
{
	false
}

fun Context.isAppRunning(packageName : String) : String
{
	val pm : PackageManager = packageManager
	val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
	var running = "App Not Installed"
	for (packageInfo in packages)
	{
		if (packageInfo.packageName == packageName)
		{
			running = if (!isSTOPPED(packageInfo)) "True" else "False"
		}
	}

	return running
}

private fun isSTOPPED(pkgInfo : ApplicationInfo) : Boolean
{
	return pkgInfo.flags and ApplicationInfo.FLAG_STOPPED != 0
}

fun AppCompatActivity.checkSelfPermissionCompat(permission : String) =
	ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission : String) =
	ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(permissionsArray : Array<String>, requestCode : Int
)
{
	ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}


fun AppCompatActivity.gotoActivity(targetActivityClass: Class<*>) {
	val intent = Intent(this, targetActivityClass)
	startActivity(intent)
}

fun Context.isValidGlideContext() = this !is Activity || (!this.isDestroyed && !this.isFinishing)
