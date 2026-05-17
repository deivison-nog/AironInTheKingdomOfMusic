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
import com.info85.aironmusic.util.StoryPrefs

class MapSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.map_select_title)

        val cleared = StoryPrefs.getRegionsCleared(this)

        binding.rvRegions.layoutManager = LinearLayoutManager(this)
        binding.rvRegions.adapter = RegionAdapter(
            regions = GameData.REGIONS,
            clearedCount = cleared
        ) { region ->
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra(GameActivity.EXTRA_REGION_INDEX, region.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh list so newly cleared regions update their badge
        val cleared = StoryPrefs.getRegionsCleared(this)
        (binding.rvRegions.adapter as? RegionAdapter)?.updateCleared(cleared)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

// ── RecyclerView adapter ──────────────────────────────────────────────────────

class RegionAdapter(
    private val regions: List<Region>,
    private var clearedCount: Int,
    private val onClick: (Region) -> Unit
) : RecyclerView.Adapter<RegionAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView        = view.findViewById(R.id.tv_region_icon)
        val tvName: TextView        = view.findViewById(R.id.tv_region_name)
        val tvDesc: TextView        = view.findViewById(R.id.tv_region_description)
        val tvEnemy: TextView       = view.findViewById(R.id.tv_region_enemy)
        val tvModes: TextView       = view.findViewById(R.id.tv_region_modes)
        val tvFragment: TextView    = view.findViewById(R.id.tv_region_fragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_region, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = regions[position]
        val isCleared  = position < clearedCount
        val isUnlocked = position <= clearedCount   // next available region is also unlocked

        holder.tvIcon.text  = r.icon
        holder.tvName.text  = r.name
        holder.tvEnemy.text = "${r.bardoName}  ·  ${r.bardoTitle}"

        if (isUnlocked) {
            holder.tvDesc.text     = r.description
            holder.tvModes.text    = if (isCleared) "✅" else "▶"
            holder.tvFragment.text = if (isCleared) "🎵 ${r.fragmentName} obtido" else ""
            holder.itemView.alpha  = 1f
            holder.itemView.setOnClickListener { onClick(r) }
        } else {
            holder.tvDesc.text     = "🔒  Derrote o Bardo anterior para desbloquear."
            holder.tvModes.text    = "🔒"
            holder.tvFragment.text = ""
            holder.itemView.alpha  = 0.45f
            holder.itemView.setOnClickListener(null)
        }

        // Tint card background with region colour
        try {
            val base = Color.parseColor(r.colorHex)
            // darken slightly so white text remains legible
            val darkened = darken(base, if (isUnlocked) 0.55f else 0.30f)
            holder.itemView.setBackgroundColor(darkened)
        } catch (_: Exception) {
            holder.itemView.setBackgroundColor(Color.DKGRAY)
        }
    }

    override fun getItemCount() = regions.size

    fun updateCleared(newCount: Int) {
        clearedCount = newCount
        notifyDataSetChanged()
    }

    private fun darken(color: Int, factor: Float): Int {
        val r = (Color.red(color)   * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color)  * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }
}
