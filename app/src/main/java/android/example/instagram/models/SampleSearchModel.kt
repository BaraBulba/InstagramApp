package android.example.instagram.models

import ir.mirrajabi.searchdialog.core.Searchable

class SampleSearchModel(private var mTitle:String?): Searchable {
    override fun getTitle(): String {
        return mTitle!!
    }

}