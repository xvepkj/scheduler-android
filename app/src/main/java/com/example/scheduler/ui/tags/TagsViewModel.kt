package com.example.scheduler.ui.tags

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.scheduler.core.Date
import com.example.scheduler.core.Tag
import io.paperdb.Paper

class TagsViewModel : ViewModel() {
    // TODO: Add color info to tag (currently only name)
    val tags : MutableList<Tag>
      get() = Paper.book("tags").read("list")

    init {
        if (!Paper.book("tags").contains("list")) {
            // assume that stats is also not initialized
            val tagList : MutableList<Tag> = mutableListOf()
            val statList : MutableList<MutableMap<Date, Pair<Long, Long>>> = mutableListOf()

            // id of misc is 0
            tagList.add(Tag("Misc", Color.WHITE))
            statList.add(mutableMapOf())

            Paper.book("tags").write("list", tagList)
            Paper.book("stats").write("list", statList)
        }
    }

    fun get(id: Int): Tag {
        return tags[id]
    }

    fun add(tag: Tag){
        // Obtain lists
        val tagList : MutableList<Tag> = Paper.book("tags").read("list")
        val statList : MutableList<MutableMap<Date, Pair<Long, Long>>> = Paper.book("stats").read("list")

        // Modify
        tagList.add(tag)
        statList.add(mutableMapOf())

        // Persist
        Paper.book("tags").write("list", tagList)
        Paper.book("stats").write("list", statList)
    }

    fun remove(index: Int) {
        val tagList : MutableList<Tag> = Paper.book("tags").read("list")
        if (index <= 0 || index >= tagList.size) {
            // Show in log maybe
            return
        }
        tagList[index].isActive = false
        Paper.book("tags").write("list", tagList)
    }

    fun modify(index: Int, tag: Tag) {
        val tagList : MutableList<Tag> = Paper.book("tags").read("list")
        if (index <= 0 || index >= tagList.size) {
            // Show in log maybe
            return
        }
        tagList[index] = tag
        Paper.book("tags").write("list", tagList)
    }
}