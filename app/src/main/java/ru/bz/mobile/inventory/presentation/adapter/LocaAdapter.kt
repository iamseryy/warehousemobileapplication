package ru.bz.mobile.inventory.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.databinding.ItemLocaBinding

import ru.bz.mobile.inventory.model.locas.Loca

class LocaDiffUtil(
    private val oldList: List<Loca>,
    private val newList: List<Loca>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.id == new.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldClot = oldList[oldItemPosition]
        val newClot = newList[newItemPosition]
        return oldClot == newClot
    }
}

interface LocaActionListener {
    fun onClick(loca: Loca)
    fun onLongClick(loca: Loca)
}

class LocaAdapter( private val locaActionListener: LocaActionListener) :
    RecyclerView.Adapter<LocaAdapter.LocaViewHolder>(), View.OnClickListener, View.OnLongClickListener {

    var data: List<Loca> = emptyList()
        set(newValue) {
            val locaDiffUtil = LocaDiffUtil(field, newValue)
            val locaDiffUtilResult = DiffUtil.calculateDiff(locaDiffUtil)
            field = newValue
            locaDiffUtilResult.dispatchUpdatesTo(this@LocaAdapter)
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocaBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)

        return LocaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocaViewHolder, position: Int) {
        val loca = data[position]
        val context = holder.itemView.context

        with(holder.binding) {
            holder.itemView.tag = loca

            locaText.text = loca.loca

            qstrText.text = "${loca.qstr} (${loca.unit})"
            qntyText.text = "${loca.qnty} (${loca.unit})"
            card.isChecked = loca.isChecked

            card.run {
                if (loca.utcDate > 0) {
                    setCardBackgroundColor(context.resources.getColor(R.color.mint))
                } else {
                    setCardBackgroundColor(context.resources.getColor(R.color.white))
                }
            }
        }
    }

    override fun onClick(view: View) {
        val loca: Loca = view.tag as Loca
        locaActionListener.onClick(loca)
    }
    override fun onLongClick(view: View): Boolean {
        val loca: Loca = view.tag as Loca

        locaActionListener.onLongClick(loca)
        return true
    }

    class LocaViewHolder(val binding: ItemLocaBinding) : RecyclerView.ViewHolder(binding.root)
}