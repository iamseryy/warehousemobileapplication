package ru.bz.mobile.inventory.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.databinding.ItemClotBinding

import ru.bz.mobile.inventory.model.clots.Clot

class ClotDiffUtil(
    private val oldList: List<Clot>,
    private val newList: List<Clot>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldClot = oldList[oldItemPosition]
        val newClot = newList[newItemPosition]
        return oldClot.id == newClot.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldClot = oldList[oldItemPosition]
        val newClot = newList[newItemPosition]
        return oldClot == newClot
    }
}

interface ClotActionListener {
    fun onClick(clot: Clot)
    fun onLongClick(clot: Clot)
}

class ClotAdapter(private val clotActionListener: ClotActionListener) :
    RecyclerView.Adapter<ClotAdapter.ClotViewHolder>(), View.OnClickListener, View.OnLongClickListener {

    var data: List<Clot> = emptyList()
        set(newValue) {
            val clotDiffUtil = ClotDiffUtil(field, newValue)
            val clotDiffUtilResult = DiffUtil.calculateDiff(clotDiffUtil)
            field = newValue
            clotDiffUtilResult.dispatchUpdatesTo(this@ClotAdapter)
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClotViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemClotBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)
        binding.locasBtn.setOnClickListener {
            onLongClick(binding.root)
        }

        return ClotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClotViewHolder, position: Int) {
        val clot = data[position]
        val context = holder.itemView.context

        with(holder.binding) {
            holder.itemView.tag = clot

            pornText.text = clot.porn
            clotText.text = clot.clot

            locasBtn.run {
                text = if(clot.isEnabled) resources.getString(R.string.locas) else clot.loca
                isEnabled = clot.isEnabled
            }
            qstrText.text = "${clot.qstrSum} (${clot.unit})"
            qntyText.text = "${clot.qntySum} (${clot.unit})"
            card.isChecked = clot.isChecked

            qstrLayout.run {
                isVisible = clot.qstrSum > 0
            }

            card.run {
                if (clot.utcDate > 0) {
                    setCardBackgroundColor(context.resources.getColor(R.color.mint))
                } else {
                    setCardBackgroundColor(context.resources.getColor(R.color.white))
                }
            }
        }
    }

    override fun onClick(view: View) {
        val clot: Clot = view.tag as Clot
        clotActionListener.onClick(clot)
    }
    override fun onLongClick(view: View): Boolean {
        val clot: Clot = view.tag as Clot

        if(!clot.isEnabled)
            return true

        clotActionListener.onLongClick(clot)
        return true
    }

    class ClotViewHolder(val binding: ItemClotBinding) : RecyclerView.ViewHolder(binding.root)
}