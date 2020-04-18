package com.example.ageinminute

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.view.View as View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {view ->
            clickDate(view )
            Toast.makeText(this,"button works",Toast.LENGTH_LONG).show() }
    }
    fun clickDate (view: View)
    {
        val  myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        val month = myCalendar.get(Calendar.MONTH)



        val dpd = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener{
                    view, syear, smonth, sdayOfMonth ->


                val selectedDate ="$sdayOfMonth/${smonth +1}/$syear"

                tvSelectedDate.setText(selectedDate)

                val sdf = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)

                val theDate = sdf.parse(selectedDate)

                val dateInMinute = theDate!!.time/60000

                val currentdate = sdf.parse(sdf.format(System.currentTimeMillis()))

                val currentDateInMinutes = currentdate!!.time/60000

                val difference = currentDateInMinutes - dateInMinute

                tvdateInMinute.setText(difference.toString())

            }
            ,year,month,day)

        /*The folowing line would restrict the user to select the future date but to select current date day
        if want to restrict to's date use "dpd.datePicker.setMaxDate(Date().time - 84600000)" */

        dpd.datePicker.setMaxDate(Date().time )

        dpd.show()
    }
}
