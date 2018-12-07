package com.example.pavel.ugadayka.Interface

import com.example.pavel.ugadayka.Model.CurrentQuestion

interface IAnswerSelect {
    fun selectedAnswer():CurrentQuestion
    fun showCorrectAnswer()
    fun disableAnswer()
    fun resetQuestion()
}