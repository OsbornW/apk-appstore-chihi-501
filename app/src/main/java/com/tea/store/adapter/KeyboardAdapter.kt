package com.tea.store.adapter

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.tea.store.R
import com.tea.store.bean.KeyItem
import com.tea.store.utils.otherwise
import com.tea.store.utils.yes
import java.util.Locale

class KeyboardAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<KeyItem>
) : RecyclerView.Adapter<KeyboardAdapter.Holder>() {
    private var callback: Callback? = null
    var type = TYPE_ENG
        private set
    var isUPCaseKey = false
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val lm = recyclerView.layoutManager as GridLayoutManager?
        lm!!.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return dataList[position].spanSize
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_keyboard, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun isUPCase(): Boolean {
        return isUPCaseKey
    }

    fun setUPCase(UPCase: Boolean) {
        isUPCaseKey = UPCase
        notifyItemRangeChanged(0, itemCount)
    }

    fun replace(list: List<KeyItem>?, type: Int) {
        dataList.clear()
        dataList.addAll(list!!)
        notifyDataSetChanged()
        this.type = type
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextView: TextView
        private val mIconView: ImageView

        init {
            mTextView = itemView.findViewById(R.id.text)
            mIconView = itemView.findViewById(R.id.icon)
        }

        fun bind(bean: KeyItem) {
            itemView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                mTextView.isSelected = hasFocus
                mIconView.isSelected = hasFocus
                setIcon(bean)
            }
            itemView.setOnClickListener {
                if (callback != null) callback!!.onClick(
                    bean,
                    mTextView.text.toString()
                )
            }
            mTextView.text = if (isUPCaseKey || bean.type == KeyItem.TYPE_SEARCH) bean.name.uppercase(
                Locale.getDefault()
            ) else bean.name.lowercase(Locale.getDefault())
            mIconView.visibility = if (bean.isUseIcon) View.VISIBLE else View.GONE
            setIcon(bean)
        }

        private fun setIcon(bean: KeyItem) {
            if (!bean.isUseIcon || bean.icon == -1) return
            val isCase = bean.type == KeyItem.TYPE_UPCAST
            var icon = bean.icon
            if (isCase) {
                icon =
                    if (isUPCaseKey) R.drawable.baseline_up_case_100 else R.drawable.baseline_lower_cast_100
            }
            val drawable = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M).yes {
                context.resources.getDrawable(icon, Resources.getSystem().newTheme())
            }.otherwise {
                context.resources.getDrawable(icon)
            }
            DrawableCompat.setTint(
                drawable,
                if (mIconView.isSelected){
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M).yes {
                        context.resources.getColor(
                            R.color.ico_style_3,
                            Resources.getSystem().newTheme()
                        )
                    }.otherwise {
                        context.resources.getColor(
                            R.color.ico_style_3
                        )
                    }
                }  else{
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M).yes {
                        context.resources.getColor(
                            R.color.ico_style_1,
                            Resources.getSystem().newTheme()
                        )
                    }.otherwise {
                        context.resources.getColor(
                            R.color.ico_style_1
                        )
                    }
                }
            )
            mIconView.setImageDrawable(drawable)
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onClick(bean: KeyItem, text: String)
    }

    companion object {
        const val TYPE_ENG = 0
        const val TYPE_NUM = 1
    }
}
