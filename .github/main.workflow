workflow "compile app" {
  on = "push"
  resolves = ["android"]
}

action "android" {
  uses = "Raul6469/android-gradle-action@master"
  secrets = ["ANDROID_LICENCE"]
  runs = "build"
}
