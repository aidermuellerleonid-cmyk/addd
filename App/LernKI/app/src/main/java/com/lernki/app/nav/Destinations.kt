package com.lernki.app.nav

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object Summarize : Destination("summarize")
    data object MathSolver : Destination("math_solver")
    data object LearningMode : Destination("learning_mode")
    data object Chat : Destination("chat")
    data object History : Destination("history")
    data object ImageAnalysis : Destination("image_analysis")
}
