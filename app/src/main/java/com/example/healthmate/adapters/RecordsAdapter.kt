package com.example.healthmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.R
import com.example.healthmate.models.HealthRecord

class RecordsAdapter(
    private val recordsList: List<HealthRecord>,
    private val onEditClick: (HealthRecord) -> Unit,
    private val onDeleteClick: (HealthRecord) -> Unit
) : RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRecordDate: TextView = itemView.findViewById(R.id.tvRecordDate)
        val tvRecordSteps: TextView = itemView.findViewById(R.id.tvRecordSteps)
        val tvRecordCalories: TextView = itemView.findViewById(R.id.tvRecordCalories)
        val tvRecordWater: TextView = itemView.findViewById(R.id.tvRecordWater)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = recordsList[position]
        holder.tvRecordDate.text = record.date
        holder.tvRecordSteps.text = "${record.steps} steps"
        holder.tvRecordCalories.text = "${record.calories} kcal"
        holder.tvRecordWater.text = "${record.waterMl} ml"

        holder.btnEdit.setOnClickListener { onEditClick(record) }
        holder.btnDelete.setOnClickListener { onDeleteClick(record) }
    }

    override fun getItemCount(): Int = recordsList.size
}