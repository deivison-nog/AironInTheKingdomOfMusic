package com.info85.aironmusic.model

data class Challenge(
    val type: ChallengeType,
    val prompt: String,
    val answer: List<String>,
    val input: MutableList<String> = mutableListOf()
) {
    val progress: Int get() = input.size
    val total: Int get() = answer.size
    val isComplete: Boolean get() = input.size == answer.size
}
