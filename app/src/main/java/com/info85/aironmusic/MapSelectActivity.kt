package com.info85.aironmusic

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.info85.aironmusic.databinding.ActivityMapSelectBinding
import com.info85.aironmusic.model.GameData
import com.info85.aironmusic.model.Region
import com.info85.aironmusic.util.StoryPrefs

class MapSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapSelectBinding
    private var clearedCount: Int = 0
    private var selectedRegion: Region? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.map_select_title)

        clearedCount = StoryPrefs.getRegionsCleared(this)
        setupMap()
        binding.btnEnterRegion.setOnClickListener {
            selectedRegion?.takeIf { it.id <= clearedCount }?.let(::openRegion)
        }
    }

    override fun onResume() {
        super.onResume()
        clearedCount = StoryPrefs.getRegionsCleared(this)
        binding.kingdomMap.updateClearedCount(clearedCount)
        val fallback = selectedRegion?.let { GameData.REGIONS.getOrNull(it.id) }
            ?: GameData.REGIONS.getOrNull(clearedCount.coerceAtMost(GameData.REGIONS.lastIndex))
        fallback?.let(::showRegion)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupMap() {
        binding.kingdomMap.setRegions(GameData.REGIONS, clearedCount)
        binding.kingdomMap.onRegionTap = { region, isUnlocked ->
            showRegion(region)
            if (!isUnlocked) {
                Toast.makeText(this, R.string.map_select_locked, Toast.LENGTH_SHORT).show()
            }
        }

        val initialRegion = GameData.REGIONS.getOrNull(clearedCount.coerceAtMost(GameData.REGIONS.lastIndex))
            ?: GameData.REGIONS.first()
        showRegion(initialRegion)
    }

    private fun showRegion(region: Region) {
        selectedRegion = region
        binding.kingdomMap.selectedRegionId = region.id

        val isCleared = region.id < clearedCount
        val isUnlocked = region.id <= clearedCount

        binding.tvSelectedRegion.text = "${region.icon} ${region.name}"
        binding.tvSelectedDesc.text = buildString {
            append(region.description)
            append("\n\n")
            append("${region.bardoName} · ${region.bardoTitle}")
        }
        binding.tvSelectedStatus.text = when {
            isCleared -> getString(R.string.map_select_cleared)
            isUnlocked -> getString(R.string.map_select_unlocked)
            else -> getString(R.string.map_select_locked)
        }

        binding.btnEnterRegion.isEnabled = isUnlocked
        binding.btnEnterRegion.alpha = if (isUnlocked) 1f else 0.55f
    }

    private fun openRegion(region: Region) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(GameActivity.EXTRA_REGION_INDEX, region.id)
        startActivity(intent)
    }
}
