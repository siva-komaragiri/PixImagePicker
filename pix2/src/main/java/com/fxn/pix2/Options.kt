package com.fxn.pix2

import android.os.Parcel
import android.os.Parcelable

/**
 * @author : Akshay Sharma
 * @since : 31/1/21, Sun
 * akshay2211.github.io
 **/
data class Options(var requestCode: Int = 0) : Parcelable {

    var spanCount: Int = 4
    var count: Int = 1

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        spanCount = parcel.readInt()
        count = parcel.readInt()
    }

    enum class Mode {
        All, Picture, Video
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(requestCode)
        parcel.writeInt(spanCount)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Options> {
        override fun createFromParcel(parcel: Parcel): Options {
            return Options(parcel)
        }

        override fun newArray(size: Int): Array<Options?> {
            return arrayOfNulls(size)
        }
    }
}
