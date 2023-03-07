package pk.com.informatica.boschprofile.view.ktx

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import java.net.MalformedURLException
import java.net.URL


fun View.makeVisible() { this.visibility = View.VISIBLE }
fun View.makeInvisible() { this.visibility = View.INVISIBLE }
fun View.makeGone() { this.visibility = View.GONE }
fun View.enable() { isEnabled = true }
fun View.disable() { isEnabled = false }
fun View.isVisible() : Boolean = this.visibility == View.VISIBLE
fun View.toggleVisibility()
{
    if (this.visibility == View.VISIBLE)
    {
        this.visibility = View.GONE
    }
    else
    {
        this.visibility = View.VISIBLE
    }
}

fun EditText.clear() { text.clear() }
fun EditText.getTextValue(): String = text.toString()
fun TextView.getTextValue(): String = text.toString()
fun EditText.moveCursorToEnd() = setSelection(text.length)
fun EditText.moveCursorToStart() = setSelection(0)
fun TextView.clear() { text = "" }

fun View.onClick(onClick: (View) -> Unit) = setOnClickListener {
    it.setDelayClick()
    onClick(it)
}

fun View.makeScrollableInsideScrollView()
{
    setOnTouchListener { v, event ->
        v.parent.requestDisallowInterceptTouchEvent(true)
        when (event.action and MotionEvent.ACTION_MASK)
        {
            MotionEvent.ACTION_UP ->
            {
                v.parent.requestDisallowInterceptTouchEvent(false)
                //It is required to call performClick() in onTouch event.
                performClick()
            }
        }
        false
    }
}

fun View.onLongClick(onLongClick: (View) -> Unit) = setOnLongClickListener {
    onLongClick(it)
    false
}

fun View.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun TextView.setDrawable(
    @DrawableRes left: Int = 0,
    @DrawableRes right: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes bottom: Int = 0
) {
    this.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

inline fun View.waitForLayout(crossinline yourAction : () -> Unit) {
    val vto = viewTreeObserver
    vto.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener
            {
                override fun onGlobalLayout()
                {
                    when
                    {
                        vto.isAlive ->
                        {
                            vto.removeOnGlobalLayoutListener(this)
                            yourAction()
                        }
                        else        -> viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            })
}

fun View.addCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

/**
 * Extension method to replace all text inside an [Editable] with the specified [newValue].
 */
fun Editable.replaceAll(newValue : String) {
    replace(0, length, newValue)
}

/**
 * Extension method to replace all text inside an [Editable] with the specified [newValue] while
 * ignoring any [android.text.InputFilter] set on the [Editable].
 */
fun Editable.replaceAllIgnoreFilters(newValue : String) {
    val currentFilters = filters
    filters = emptyArray()
    replaceAll(newValue)
    filters = currentFilters
}

/**
 * returns EditText text as URL
 */
fun EditText.getUrl(): URL? {
    return try {
        URL(text.toString())
    } catch (e : MalformedURLException) {
        null
    }
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.disableCopyAndPaste() {
    try {
        
        this.setOnLongClickListener { _ -> true }
        this.isLongClickable = false
        this.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                this.setInsertionDisabled()
            }
            false
        }
        this.setTextIsSelectable(false)
        this.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(mode : ActionMode?, item : MenuItem?): Boolean {
                return false
            }
            
            override fun onPrepareActionMode(mode : ActionMode?, menu : Menu?): Boolean {
                return false
            }
            
            override fun onCreateActionMode(mode : ActionMode, menu : Menu): Boolean {
                return false
            }
            
            override fun onDestroyActionMode(mode : ActionMode) {
            
            }
        }
        
    } catch (e : Exception) {
        e.printStackTrace()
    }
}

fun EditText.setInsertionDisabled() {
    try {
        val editorField = TextView::class.java.getDeclaredField("mEditor")
        editorField.isAccessible = true
        val editorObject = editorField.get(this)
        
        // if this view supports insertion handles
        @SuppressLint("PrivateApi") val editorClass = Class.forName("android.widget.Editor")
        val mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled")
        mInsertionControllerEnabledField.isAccessible = true
        mInsertionControllerEnabledField.set(editorObject, false)
        
        // if this view supports selection handles
        val mSelectionControllerEnabledField = editorClass.getDeclaredField("mSelectionControllerEnabled")
        mSelectionControllerEnabledField.isAccessible = true
        mSelectionControllerEnabledField.set(editorObject, false)
    } catch (e : Exception) {
        e.printStackTrace()
    }
}

fun EditText.clearError() {
    error = null
}

fun deleteAllWhenContainsStar(vararg editTexts : EditText) {
    for (et in editTexts) {
        et.deleteAllWhenContainsStar()
    }
}

fun EditText.deleteAllWhenContainsStar() {
    this.setOnKeyListener { _, keyCode, _ ->
        if (keyCode == KeyEvent.KEYCODE_DEL && this.text.toString().contains("*")) {
            this.setText("")
        }
        false
    }
}

fun EditText.setReadOnly(readOnly : Boolean, inputType : Int = InputType.TYPE_NULL) {
    isFocusable = !readOnly
    isFocusableInTouchMode = !readOnly
    this.inputType = inputType
}

fun View.setMargins(left : Int, top : Int, right : Int, bottom : Int)
{
    val layoutParams = (this.layoutParams as ViewGroup.MarginLayoutParams)
    layoutParams.setMargins(left, top, right, bottom)
    this.layoutParams = layoutParams
}

fun View.playAnimation(anim : Int) { startAnimation(AnimationUtils.loadAnimation(context, anim)) }

fun View.toggleShowHide(viewGroup : ViewGroup, hide : Boolean, duration: Long = 500, animation: Int = Gravity.BOTTOM)
{
    val transition : Transition = Slide(animation)
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(viewGroup, transition)
    this.visibility = if (hide) View.GONE else View.VISIBLE
}

fun View.setViewBackgroundWithoutResettingPadding(@DrawableRes backgroundResId : Int) {
    val paddingBottom = this.paddingBottom
    val paddingStart = ViewCompat.getPaddingStart(this)
    val paddingEnd = ViewCompat.getPaddingEnd(this)
    val paddingTop = this.paddingTop
    setBackgroundResource(backgroundResId)
    ViewCompat.setPaddingRelative(this, paddingStart, paddingTop, paddingEnd, paddingBottom)
}

fun ScrollView.moveScrollViewUp()
{
    isFocusableInTouchMode = true
    fullScroll(View.FOCUS_UP)
    smoothScrollTo(0, 0)
}

fun NestedScrollView.moveScrollViewUp()
{
    isFocusableInTouchMode = true
    fullScroll(View.FOCUS_UP)
    smoothScrollTo(0, 0)
}

fun View.setViewBackgroundWithoutResettingPadding(background : Drawable?) {
    val paddingBottom = this.paddingBottom
    val paddingStart = ViewCompat.getPaddingStart(this)
    val paddingEnd = ViewCompat.getPaddingEnd(this)
    val paddingTop = this.paddingTop
    ViewCompat.setBackground(this, background)
    ViewCompat.setPaddingRelative(this, paddingStart, paddingTop, paddingEnd, paddingBottom)
}

fun View.setDelayClick(delay : Long = 500)
{
    isEnabled = false
    Handler(Looper.getMainLooper()).postDelayed({ isEnabled = true }, delay)
}

fun View.setBgColor(viewContext : Context, color : Int)
{
    setBackgroundColor(ContextCompat.getColor(viewContext, color))
}

fun EditText.afterTextChanged(afterTextChanged : (String) -> Unit) {
    this.addTextChangedListener(
        object : TextWatcher
        {
            override fun beforeTextChanged(p0 : CharSequence?, p1 : Int, p2 : Int, p3 : Int)
            {
            }

            override fun onTextChanged(p0 : CharSequence?, p1 : Int, p2 : Int, p3 : Int)
            {
            }

            override fun afterTextChanged(editable : Editable?)
            {
                afterTextChanged.invoke(editable.toString())
            }
        })
}

/**
 * Remember to add this permission to the Manifest:
 * <uses-permission android:name="android.permission.VIBRATE"/>
 */
inline fun View.consumeButtonWithHapticFeedback(lambda : () -> Unit) {
    lambda()
    this.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
}

fun TextView.bold()
{
    setTypeface(this.typeface, Typeface.BOLD)
}

fun TextView.unBold()
{
    typeface = Typeface.DEFAULT
}

fun TextView.deleteLine() {
    paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
    paint.isAntiAlias = true
}

fun TextView.underLine() {
    paint.flags = paint.flags or Paint.UNDERLINE_TEXT_FLAG
    paint.isAntiAlias = true
}

fun TextView.setSize(size : Float) { setTextSize(TypedValue.COMPLEX_UNIT_SP, size) }

fun RecyclerView.disableItemAnimator() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

fun ListView.justifyListViewHeightBasedOnChildren() {

    val adapter = adapter ?: return

    val vg = this

    val desiredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.UNSPECIFIED)

    var totalHeight = 0

    for (i in 0 until adapter.count-1) {
        val listItem = adapter.getView(i, null, vg)

        listItem.measure(0, 0)

        totalHeight += listItem.measuredHeight
    }

    val par = layoutParams
    par.height = totalHeight + (dividerHeight * (adapter.count - 1))
    layoutParams = par
    requestLayout()
}

fun View.getScreenShot(): Bitmap {
    val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable = background
    if (bgDrawable != null) bgDrawable.draw(canvas)
    else canvas.drawColor(Color.WHITE)
    draw(canvas)
    return returnedBitmap
}

fun View.validate() : Boolean
{
    when (this)
    {
        is EditText ->
        {
            return text.isEmpty()
        }
        is TextView ->
        {
            return text.isEmpty()
        }
        is CheckBox ->
        {
            return isChecked
        }
        else        ->
            return false
    }
}

fun View.disableAllElements(closure: (View) -> Unit) {
    closure(this)
    val groupView = this as? ViewGroup ?: return
    val size = groupView.childCount - 1
    for (i in 0..size) {
        groupView.getChildAt(i).disableAllElements(closure)
    }
}

/**
 * Sets ListView height dynamically based on the height of the items.
 *
 * @param listView to be resized
 * @return true if the listView is successfully resized, false otherwise
 */
fun setListViewHeightBasedOnItems(listView: ListView): Boolean {
    val listAdapter = listView.adapter
    return if (listAdapter != null) {
        val numberOfItems = listAdapter.count

        // Get total height of all items.
        var totalItemsHeight = 0
        for (itemPos in 0 until numberOfItems) {
            val item = listAdapter.getView(itemPos, null, listView)
            item.measure(0, 0)
            totalItemsHeight += item.measuredHeight
        }

        // Get total height of all item dividers.
        val totalDividersHeight = listView.dividerHeight *
                (numberOfItems - 1)

        // Set list height.
        val params = listView.layoutParams
        params.height = totalItemsHeight + totalDividersHeight
        listView.layoutParams = params
        listView.requestLayout()
        true
    } else {
        false
    }
}

fun View.hideKeyboard(){
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

@SuppressLint("ServiceCast")
fun isNetworkConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting
}

fun Context.isEthernetConnected(): Boolean? {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        return cm!!.activeNetworkInfo!!.type == ConnectivityManager.TYPE_ETHERNET
    return false
}

const val TAG = "connectivity"
fun checkInternetConnectivity(context: Context) : Boolean
{
    val connectionManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {
        val networkCapabilities = connectionManager.getNetworkCapabilities(connectionManager.activeNetwork)

        return if (networkCapabilities == null)
        {
            Log.i(TAG, "device is offline")
            false
        }
        else
        {
            Log.i(TAG, "device is online")
            if (
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
            )
            {
                when
                {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->
                    {
                        Log.d(TAG, "Connected to Wifi")
                    }
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->
                    {
                        Log.d(TAG, "Connected to Mobile data")
                    }
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->
                    {
                        Log.d(TAG, "Connected to Lan")
                    }
                    else->
                    {
                        Log.d(TAG, "Unknown Networkd")
                    }
                }
                return true

            }
            else
            {
                Log.i(TAG, "Not Connected to Internet")
                false
            }
        }
    }
    return false
}

