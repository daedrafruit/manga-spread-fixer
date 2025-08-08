plugins {
    java
		application
}

repositories {
    mavenCentral()
}

dependencies {
    // twelvemonkeys for jpeg file support
    implementation("com.twelvemonkeys.imageio:imageio-jpeg:3.10.1")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
}

application {
    mainClass.set("TwoPageSpreadFixerGUI")
}


