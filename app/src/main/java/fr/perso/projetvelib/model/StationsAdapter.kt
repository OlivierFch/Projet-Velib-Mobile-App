package fr.perso.projetvelib.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.perso.projetvelib.DetailsStationActivity
import fr.perso.projetvelib.R

class StationsAdapter(var items: List<Station>) : RecyclerView.Adapter<StationsAdapter.StationsViewHolder>(), Filterable {

    var stationsFilteredList: List<Station> = ArrayList()

    init {
        stationsFilteredList = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationsViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_station, parent, false)
        return StationsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StationsViewHolder, position: Int) {

        val station = stationsFilteredList[position]

        holder.bind(station)

        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, DetailsStationActivity::class.java)
            intent.putExtra("id", position)
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = stationsFilteredList.size


    inner class StationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var stationName: TextView

        init {
            stationName = itemView.findViewById(R.id.textViewNameStation)
        }

        fun bind(station: Station) {
            stationName.text = station.name
        }

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    stationsFilteredList = items
                }else {
                    var resultList = ArrayList<Station>()
                    for(station in items) {
                        if (station.name.lowercase().contains(charSearch.lowercase())) {
                            resultList.add(station)
                        }
                    }
                    stationsFilteredList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = stationsFilteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                stationsFilteredList = results?.values as ArrayList<Station>
                notifyDataSetChanged()
            }

        }
    }

}