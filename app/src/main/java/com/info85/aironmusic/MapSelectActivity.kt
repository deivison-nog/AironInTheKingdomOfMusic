package com.info85.aironmusic

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.info85.aironmusic.databinding.ActivityMapSelectBinding
import com.info85.aironmusic.model.GameData
import com.info85.aironmusic.model.Region

class MapSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.map_select_title)

        binding.rvRegions.layoutManager = LinearLayoutManager(this)
        binding.rvRegions.adapter = RegionAdapter(GameData.REGIONS) { region ->
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra(GameActivity.EXTRA_REGION_INDEX, region.id)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return true
    }
}

// ── RecyclerView adapter ──────────────────────────────────────────────────────

class RegionAdapter(
    private val regions: List<Region>,
    private val onClick: (Region) -> Unit
) : RecyclerView.Adapter<RegionAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView        = view.findViewById(R.id.tv_region_icon)
        val tvName: TextView        = view.findViewById(R.id.tv_region_name)
        val tvDesc: TextView        = view.findViewById(R.id.tv_region_description)
        val tvEnemy: TextView       = view.findViewById(R.id.tv_region_enemy)
        val tvModes: TextView       = view.findViewById(R.id.tv_region_modes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_region, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = regions[position]
        holder.tvIcon.text  = r.icon
        holder.tvName.text  = r.name
        holder.tvDesc.text  = r.description
        holder.tvEnemy.text = "${r.enemyIcon} ${r.enemyName}  (${r.requiredKills} derrotas)"
        holder.tvModes.text = "Modos: ${r.modes.joinToString(", ")}"

        try {
            holder.itemView.setBackgroundColor(Color.parseColor(r.colorHex))
        } catch (_: Exception) {
            holder.itemView.setBackgroundColor(Color.DKGRAY)
        }

        holder.itemView.setOnClickListener { onClick(r) }
    }

    override fun getItemCount() = regions.size
}
