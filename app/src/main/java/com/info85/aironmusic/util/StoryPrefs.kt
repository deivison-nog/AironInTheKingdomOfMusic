package com.info85.aironmusic.util

import android.content.Context

/**
 * Manages persistent story progress:
 * – how many regions have been cleared (sequential unlock)
 * – whether the intro narrative has been shown already
 */
object StoryPrefs {

    private const val PREFS_NAME = "airon_story_progress"
    private const val KEY_REGIONS_CLEARED = "regions_cleared"
    private const val KEY_INTRO_SHOWN = "intro_shown"

    /** Returns the number of regions that have been cleared (0 = none cleared yet). */
    fun getRegionsCleared(context: Context): Int =
        prefs(context).getInt(KEY_REGIONS_CLEARED, 0)

    /**
     * Marks [regionId] as cleared.  The unlock counter advances only
     * if [regionId] + 1 exceeds the current value, so replaying an
     * already-cleared region never resets progress.
     */
    fun markRegionCleared(context: Context, regionId: Int) {
        val current = getRegionsCleared(context)
        if (regionId + 1 > current) {
            prefs(context).edit().putInt(KEY_REGIONS_CLEARED, regionId + 1).apply()
        }
    }

    /** True if the intro narrative screen has already been shown. */
    fun isIntroShown(context: Context): Boolean =
        prefs(context).getBoolean(KEY_INTRO_SHOWN, false)

    fun markIntroShown(context: Context) {
        prefs(context).edit().putBoolean(KEY_INTRO_SHOWN, true).apply()
    }

    /** Wipes all progress (debug / "new game" use). */
    fun resetProgress(context: Context) {
        prefs(context).edit().clear().apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
