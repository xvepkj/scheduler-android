package com.xve.scheduler.ui.templates.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.xve.scheduler.core.ScheduleTemplate
import io.paperdb.Book
import io.paperdb.Paper

class TemplateViewModel : ViewModel() {
  private var templateBook: Book = Paper.book("templates")

  fun addTemplate (template: ScheduleTemplate) {
    templateBook.write(template.name, template)
    Log.d("DBG", getTemplateNames().toString())
  }

  fun getTemplate (name: String): ScheduleTemplate {
    return templateBook.read(name)
  }

  fun getTemplateNames(): List<String> {
    return templateBook.allKeys
  }
  fun removeTemplate(template: ScheduleTemplate){
    templateBook.delete(template.name)
  }
}