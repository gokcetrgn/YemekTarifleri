package com.gokcenaztorgan.yemektarifleri.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.gokcenaztorgan.yemektarifleri.databinding.RecyclerRowBinding
import com.gokcenaztorgan.yemektarifleri.model.Tarif
import com.gokcenaztorgan.yemektarifleri.view.ListeFragmentDirections

class TarifAdapter(val tarifList : List<Tarif>) : RecyclerView.Adapter<TarifAdapter.TarifHolder>() {
    class TarifHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarifHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TarifHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return tarifList.size
    }

    override fun onBindViewHolder(holder: TarifHolder, position: Int) {
        holder.binding.recyclerviewtext.text = tarifList[position].isim
        holder.itemView.setOnClickListener{
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment("eski",tarifList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }

}