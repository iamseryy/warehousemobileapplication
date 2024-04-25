// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

}
allprojects {
    extra.apply {
        set("coreVersion", "1.12.0")//old 1.9.0
        set("appCompatVersion", "1.6.1")
        set("activityVersion", "1.7.2")
        set("constraintLayoutVersion", "2.1.4")
        set("materialVersion", "1.9.0")//old 1.8.0
        set("roomVersion", "2.5.2")//old 2.4.1

        set("coroutinesVersion", "1.6.4")
        set("lifecycleVersion", "2.6.2")

        //com.google.mlkit:barcode-scanning:17.0.2
        set("mlkitBarcodeVersion", "17.2.0")//old 17.0.2

        // testing
        set("junitVersion", "4.13.2")
        set("espressoVersion", "3.5.1")
        set("androidxJunitVersion", "1.1.5")

        set("dataStoreVersion","1.0.0")

        set("navigationUIVersion","2.7.4")
    }
}
