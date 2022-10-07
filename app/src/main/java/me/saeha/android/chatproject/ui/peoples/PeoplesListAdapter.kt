package me.saeha.android.chatproject.ui.peoples
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import me.saeha.android.chatproject.databinding.ItemPeoplesBinding
import me.saeha.android.chatproject.model.Peoples

class PeoplesListAdapter (val context: Context?, private val peoplesList:List<Peoples>):
RecyclerView.Adapter<PeoplesListAdapter.ItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val templateDetailBinding = ItemPeoplesBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ItemViewHolder(templateDetailBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = peoplesList[position]
        holder.onBind(context, item)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PeoplesProfileActivity::class.java)
            intent.putExtra("peoplesData", item)
          	context?.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = peoplesList.size

    class ItemViewHolder(binding: ItemPeoplesBinding): RecyclerView.ViewHolder(binding.root){
        val tvPeoplesName = binding.tvPeoplesName
        val tvPeoplesPosition = binding.tvPeoplesPosition
        val ivPeoplesImage = binding.ivPeoplesImage


        fun onBind(context: Context?, item: Peoples){
            tvPeoplesName.text = item.name
            tvPeoplesPosition.text = item.position
        }
    }
}