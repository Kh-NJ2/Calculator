package com.example.calculator

import android.content.res.Configuration
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private lateinit var guideline: Guideline
    private var canAddOperation = false
    private var canAddDecimal = true
    private var canAddNumber = true
    private var canDelete = true
    private var equal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        guideline = findViewById<Guideline>(R.id.guideline)

        eqTV.movementMethod = ScrollingMovementMethod()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(windowManager.defaultDisplay.height <= 2000){
            val params = guideline.layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = 0f // set the percentage value to 25%

            guideline.layoutParams = params
        } else {
            val params = guideline.layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = 0.55f // set the percentage value to 25%

            guideline.layoutParams = params

        }

    }

    fun deleteAction(view: View) {
        var deleted = 1
        val length = eqTV.length()
        var lengthnew = eqTV.length()
        if (length > 0 && canDelete){
            eqTV.text = eqTV.text.subSequence(0, length-1)
            deleted++
            lengthnew--
            if (lengthnew >= 1) {
                when (eqTV.text[length - deleted].toString()) {
                    "×" -> canAddOperation = false
                    "+" -> canAddOperation = false
                    "-" -> canAddOperation = false
                    "÷" -> canAddOperation = false
                    "%" -> canAddOperation = false
                    "." -> canAddDecimal = false
                    else -> {
                        canAddOperation = true
                        canAddNumber = true
                    }
                }
            }

        }
    }

    fun clearAllAction(view: View) {
        eqTV.text = ""
        resultTV.text = ""
        canAddNumber = true
        canAddDecimal = true
        canDelete = true
        resultTV.setTextColor(ContextCompat.getColor(this, R.color.whitish))
        eqTV.scrollTo(0, 0)
    }

    fun changeNumberAction(view: View) {
        if(view is Button){
            if(canAddNumber) {
                if (view.text == ".") {
                    if (canAddDecimal) {
                        eqTV.append(view.text)
                        canAddDecimal = false
                        equal = 0
                    }
                } else {
                    eqTV.append(view.text)
                    equal = 0
                }
                canAddOperation = true
            }
        }
    }
    fun opAction(view: View) {
        if (view is Button){
            if (eqTV.length() > 0) {
                if (canAddOperation) {
                    eqTV.append(view.text)
                    canAddOperation = false

                    if(eqTV.text.last().toString() == "%"){
                        canAddNumber = false
                    }
            }
        }
    }
    }
    private fun getInputExpression(): String {
        var expression = eqTV.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("×"), "*")
        expression = expression.replace(Regex("%"), "/ 100 ")
        return expression
    }

    fun equalAction(view: View) {
        equal++
        try {
            val expresion = getInputExpression()
            var result = Expression(expresion).calculate()

            if (result.isNaN()) {
                eqTV.text = "Error"
                resultTV.setTextColor(ContextCompat.getColor(this, R.color.redish))
                resultTV.text = "Error"
                canAddNumber = false
                canAddDecimal = false
                canAddOperation = false
                canDelete = false
            }else{
                resultTV.text = DecimalFormat("0.########").format(result).toString()
                if(equal > 1){
                    resultTV.text = ""
                    eqTV.text = DecimalFormat("0.########").format(result).toString()
                    equal = 0
                }
            }
        }catch (e:ArithmeticException){
            resultTV.text ="Error"
            resultTV.setTextColor(ContextCompat.getColor(this, R.color.redish))
        }
    }

}