package com.tea.store.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tea.store.R
import com.tea.store.adapter.KeyboardAdapter
import com.tea.store.bean.KeyItem
import com.tea.store.utils.isAndroidAtMost5_1
import com.tea.store.utils.otherwise
import com.tea.store.utils.toTrim
import com.tea.store.utils.yes

class KeyboardDialog : SingleDialogFragment(), KeyboardAdapter.Callback {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: KeyboardAdapter? = null
    private var callback: Callback? = null
    private val ens: MutableList<KeyItem> = ArrayList()
    private val nums: MutableList<KeyItem> = ArrayList()
    private var mTargetView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fillEn()
        fillNums()
    }

    override fun getLayout(): Int {
        return R.layout.dialog_keyboard
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mRecyclerView = view.findViewById(R.id.recycler)
        mAdapter = KeyboardAdapter(requireContext(), inflater, ArrayList())
    }

    override fun initBefore(inflater: LayoutInflater, view: View) {
        super.initBefore(inflater, view)
        mAdapter!!.setCallback(this)
        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                if (event.unicodeChar == 0) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DEL -> del()
                    }
                } else {
                    mTargetView!!.append(event.displayLabel.toString())
                }
            }
            false
        }
    }

    override fun initBind(inflater: LayoutInflater, view: View) {
        super.initBind(inflater, view)
        mAdapter!!.replace(ens, KeyboardAdapter.TYPE_ENG)
        mRecyclerView!!.setLayoutManager(GridLayoutManager(activity, 10))
        mRecyclerView!!.setAdapter(mAdapter)
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    }

    override fun getWidthAndHeight(): IntArray {
        return intArrayOf(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )
    }

    override fun getDimAmount(): Float {
        return 0f
    }

    fun setTargetView(targetView: TextView?) {
        mTargetView = targetView
    }

    private fun fillEn() {
        var array =
            "q w e r t y u i o p".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        Log.e(TAG, "fillEn: " + array.contentToString())
        for (item in array) ens.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        array =
            "a s d f g h j k l ,".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) ens.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        ens.add(KeyItem(KeyItem.TYPE_UPCAST, "", R.drawable.baseline_lower_cast_100, 1, true))
        array = "z x c v b n m .".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) ens.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        ens.add(KeyItem(KeyItem.TYPE_DEL, "", R.drawable.baseline_backspace_100, 1, true))
        ens.add(KeyItem(KeyItem.TYPE_SWITCH, "?123", -1, 2, false))
        ens.add(KeyItem(KeyItem.TYPE_SPACE, " ", -1, 6, false))
        ens.add(KeyItem(KeyItem.TYPE_SEARCH, "DONE", -1, 2, true))
    }

    private fun fillNums() {
        var array =
            "1 2 3 4 5 6 7 8 9 0".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) nums.add(KeyItem(KeyItem.TYPE_NUM, item, -1, 1, false))
        array =
            "! @ # $ % ^ & * ( )".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) nums.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        array =
            "~ , . ? / \" - + =".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) nums.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        nums.add(KeyItem(KeyItem.TYPE_DEL, "", R.drawable.baseline_backspace_100, 1, true))
        nums.add(KeyItem(KeyItem.TYPE_SWITCH, "ABC", -1, 2, false))
        nums.add(KeyItem(KeyItem.TYPE_SPACE, " ", -1, 6, false))
        nums.add(KeyItem(KeyItem.TYPE_SEARCH, "DONE", -1, 2, true))
    }

    override fun onClick(bean: KeyItem, text: String) {
        when (bean.type) {
            KeyItem.TYPE_SWITCH -> if (mAdapter!!.type == KeyboardAdapter.TYPE_ENG) {
                mAdapter!!.replace(nums, KeyboardAdapter.TYPE_NUM)
            } else if (mAdapter!!.type == KeyboardAdapter.TYPE_NUM) {
                mAdapter!!.replace(ens, KeyboardAdapter.TYPE_ENG)
            }

            KeyItem.TYPE_UPCAST -> mAdapter!!.setUPCase(!mAdapter!!.isUPCaseKey)
            KeyItem.TYPE_DEL -> {
                //"按下了删除==".d("zy2001")
                del()
            }
            KeyItem.TYPE_SEARCH -> {
                //"按下了搜索==".d("zy2001")
                mTargetView!!.onEditorAction(EditorInfo.IME_ACTION_DONE)
                dismiss()
            }

            KeyItem.TYPE_SPACE->{
                //"按下了空格==".d("zy2001")
                mTargetView!!.append(" ")
            }

            else -> {
                isAndroidAtMost5_1().yes {
                    //mTargetView!!.append(text.toTrim())
                    //mTargetView?.text = mTargetView?.text?.toString()?.toTrim()
                    mTargetView?.text = mTargetView?.text.toString().toTrim() + text.toTrim()
                }.otherwise {
                    Log.e("zengyue", "onClick: 添加了字符", )
                    mTargetView!!.append(text)
                    //mTargetView?.text = mTargetView?.text?.toString()
                }


            }
        }
    }

    private fun del() {

        isAndroidAtMost5_1().yes { mTargetView?.text = mTargetView?.text?.toString()?.toTrim() }


        val len = mTargetView?.text?.length
        if (len != 0) {
            var finalStr = ""
            isAndroidAtMost5_1().yes {
                val str = mTargetView?.text?.toString()?.toTrim()
                val len1 = (mTargetView?.text?.toString()?.toTrim()?.length?:0).minus(1)
                 finalStr = str?.subSequence(0, len1).toString()
            }.otherwise {
                val str = mTargetView?.text?.toString()
                val len1 = (mTargetView?.text?.toString()?.length?:0).minus(1)
                finalStr = str?.subSequence(0, len1).toString()
            }


           // "设置之后,长文字是====${str}===${finalStr}===$len1".d("zy1997")
            mTargetView?.text = finalStr

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (callback != null) callback!!.onClose()
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onClose()
    }

    companion object {
        const val TAG = "KeyboardDialog"
        @JvmStatic
        fun newInstance(): KeyboardDialog {
            val args = Bundle()
            val fragment = KeyboardDialog()
            fragment.setArguments(args)
            return fragment
        }
    }
}
