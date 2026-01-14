import { Download, Shield, Wifi, Lock, Smile, Heart, Github, Play } from 'lucide-react';

function App() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white">
      {/* Header */}
      <header className="fixed top-0 left-0 right-0 bg-gradient-to-r from-[#AB47BC] via-[#EC407A] to-[#EF5350] z-50 shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <img
             src="images/logo.png"
              alt="KidzTV Logo"
              className="h-12 w-auto"
              onError={(e) => {
                e.currentTarget.style.display = 'none';
                const fallback = e.currentTarget.nextElementSibling as HTMLElement;
                if (fallback) fallback.style.display = 'flex';
              }}
            />
            <div className="hidden items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-white/20 backdrop-blur-sm flex items-center justify-center">
                <Play className="w-6 h-6 text-white" fill="white" />
              </div>
              <span className="text-2xl font-bold text-white">
                KidzTV
              </span>
            </div>
          </div>
          <a
            href="https://github.com/eltonkola/kidztv/releases/latest/download/app-release-unsigned-signed.apk"
            target="_blank"
            rel="noopener noreferrer"
            className="px-6 py-2.5 bg-white text-[#EC407A] rounded-full font-medium hover:shadow-xl hover:scale-105 transition-all duration-300"
          >
            Download Now
          </a>
        </div>
      </header>

      {/* Hero Section */}
      <section className="pt-32 pb-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center max-w-4xl mx-auto">
            <div className="inline-flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-[#AB47BC]/10 via-[#EC407A]/10 to-[#EF5350]/10 rounded-full mb-8 border border-[#EC407A]/20">
              <Shield className="w-4 h-4 text-[#EC407A]" />
              <span className="text-sm font-medium text-gray-700">Safe & Ad-Free</span>
            </div>

            <h1 className="text-5xl sm:text-6xl lg:text-7xl font-bold mb-6 leading-tight">
              A Safe Video Player
              <br />
              <span className="bg-gradient-to-r from-[#AB47BC] via-[#EC407A] to-[#EF5350] bg-clip-text text-transparent">
                Made for Kids
              </span>
            </h1>

            <p className="text-xl text-gray-600 mb-12 leading-relaxed max-w-2xl mx-auto">
              Parent-controlled offline video player. No ads, no recommendations, no internet required after setup.
              Just safe, distraction-free entertainment for your children.
            </p>

            <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
              <a
                href="https://github.com/eltonkola/kidztv/releases/latest/download/app-release-unsigned-signed.apk"
                target="_blank"
                rel="noopener noreferrer"
                className="px-8 py-4 bg-gradient-to-r from-[#AB47BC] via-[#EC407A] to-[#EF5350] text-white rounded-full font-semibold hover:shadow-2xl hover:scale-105 transition-all duration-300 flex items-center gap-2"
              >
                <Download className="w-5 h-5" />
                Download APK
              </a>
              <a
                href="https://github.com/eltonkola/kidztv"
                target="_blank"
                rel="noopener noreferrer"
                className="px-8 py-4 bg-white border-2 border-gray-200 text-gray-700 rounded-full font-semibold hover:border-[#EC407A] hover:text-[#EC407A] transition-all duration-300 flex items-center gap-2"
              >
                <Github className="w-5 h-5" />
                View on GitHub
              </a>
            </div>
          </div>

        </div>
      </section>

      {/* Screenshots Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl sm:text-5xl font-bold mb-4">
              See It in
              <span className="bg-gradient-to-r from-[#AB47BC] via-[#EC407A] to-[#EF5350] bg-clip-text text-transparent"> Action</span>
            </h2>
            <p className="text-xl text-gray-600">Simple interface designed for young children</p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {[
              {
                title: 'Home Screen',
                description: 'Large, colorful buttons easy for kids to tap',
                image: 'images/screenshots/home.png'
              },
              {
                title: 'Video Playback',
                description: 'Distraction-free viewing experience',
                image: 'images/screenshots/playback.png'
              },
              {
                title: 'Parental Controls',
                description: 'Math-based lock protects settings',
                image: 'images/screenshots/controls.png'
              },
              {
                title: 'Video Library',
                description: 'Simple grid view of all content',
                image: 'images/screenshots/library.png'
              }
            ].map((screenshot, index) => (
              <div key={index} className="group">
                <div className="relative overflow-hidden rounded-2xl border-2 border-gray-200 group-hover:border-[#EC407A]/50 transition-all duration-300 shadow-lg group-hover:shadow-2xl bg-gradient-to-br from-gray-100 to-gray-200">
                  <div className="aspect-video relative">
                    <img
                      src={screenshot.image}
                      alt={screenshot.title}
                      className="w-full h-full object-contain"
                      onError={(e) => {
                        const target = e.currentTarget as HTMLImageElement;
                        target.style.display = 'none';
                        const fallback = target.nextElementSibling as HTMLElement;
                        if (fallback) fallback.style.display = 'flex';
                      }}
                    />
                    <div className="hidden absolute inset-0 flex-col items-center justify-center p-8">
                      <div className="w-20 h-20 mb-4 rounded-2xl bg-gradient-to-br from-[#AB47BC] via-[#EC407A] to-[#EF5350] flex items-center justify-center">
                        <Play className="w-10 h-10 text-white" />
                      </div>
                      <p className="text-sm font-medium text-gray-600 text-center">{screenshot.title}</p>
                      <p className="text-xs text-gray-500 text-center mt-2">Upload screenshot to<br />/public{screenshot.image}</p>
                    </div>
                  </div>
                </div>
                <div className="mt-4">
                  <h3 className="font-semibold text-gray-900 mb-1">{screenshot.title}</h3>
                  <p className="text-sm text-gray-600">{screenshot.description}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl sm:text-5xl font-bold mb-4">
              Everything You Need for
              <span className="bg-gradient-to-r from-[#AB47BC] via-[#EC407A] to-[#EF5350] bg-clip-text text-transparent"> Safe Viewing</span>
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Built with parents and children in mind. Simple, secure, and stress-free.
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {[
              {
                icon: Shield,
                title: 'Ad-Free Viewing',
                description: 'Zero ads, no distractions. Just pure, safe content for your kids.',
                gradient: 'from-[#AB47BC]'
              },
              {
                icon: Wifi,
                title: 'Offline Playback',
                description: 'Watch videos without an internet connection. Perfect for road trips.',
                gradient: 'from-[#EC407A]'
              },
              {
                icon: Lock,
                title: 'Parental Controls',
                description: 'Math-based lock screen protects settings. You control what they watch.',
                gradient: 'from-[#EF5350]'
              },
              {
                icon: Smile,
                title: 'Kid-Friendly Interface',
                description: 'Large buttons, simple navigation. Designed for little hands.',
                gradient: 'from-[#AB47BC]'
              },
              {
                icon: Heart,
                title: 'No Data Collection',
                description: 'Complete privacy. We don\'t track you or your children.',
                gradient: 'from-[#EC407A]'
              },
              {
                icon: Github,
                title: 'Open Source',
                description: 'Transparent, community-driven, and free forever.',
                gradient: 'from-[#EF5350]'
              }
            ].map((feature, index) => (
              <div
                key={index}
                className="group p-8 rounded-2xl border border-gray-100 hover:border-[#EC407A]/30 hover:shadow-xl transition-all duration-300 bg-gradient-to-b from-white to-gray-50"
              >
                <div className={`w-14 h-14 rounded-2xl bg-gradient-to-br ${feature.gradient} to-[#EF5350] flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-300`}>
                  <feature.icon className="w-7 h-7 text-white" />
                </div>
                <h3 className="text-xl font-bold mb-3 text-gray-900">{feature.title}</h3>
                <p className="text-gray-600 leading-relaxed">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl sm:text-5xl font-bold mb-4">
              <span className="bg-gradient-to-r from-[#AB47BC] via-[#EC407A] to-[#EF5350] bg-clip-text text-transparent">Simple</span> Setup
            </h2>
            <p className="text-xl text-gray-600">Get started in three easy steps</p>
          </div>

          <div className="grid md:grid-cols-3 gap-12">
            {[
              {
                step: '01',
                title: 'Download Videos',
                description: 'Upload your videos to YouTube (can be private/unlisted) and use the app to download them for offline viewing.'
              },
              {
                step: '02',
                title: 'Set Up Protection',
                description: 'Use the math-based lock screen to protect parental settings. Lock the screen to keep kids safely in the app.'
              },
              {
                step: '03',
                title: 'Let Them Watch',
                description: 'Hand over the device worry-free. Your children enjoy safe, curated content without any distractions.'
              }
            ].map((item, index) => (
              <div key={index} className="relative">
                <div className="text-8xl font-bold text-transparent bg-gradient-to-br from-[#AB47BC]/10 via-[#EC407A]/10 to-[#EF5350]/10 bg-clip-text mb-4">
                  {item.step}
                </div>
                <h3 className="text-2xl font-bold mb-4 text-gray-900">{item.title}</h3>
                <p className="text-gray-600 leading-relaxed">{item.description}</p>
                {index < 2 && (
                  <div className="hidden md:block absolute top-12 -right-6 w-12 h-0.5 bg-gradient-to-r from-[#EC407A] to-transparent"></div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
        <div className="max-w-4xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl sm:text-5xl font-bold mb-4">
              Frequently Asked
              <span className="bg-gradient-to-r from-[#AB47BC] via-[#EC407A] to-[#EF5350] bg-clip-text text-transparent"> Questions</span>
            </h2>
          </div>

          <div className="space-y-6">
            {[
              {
                q: 'Is an internet connection required?',
                a: 'Only for downloading videos initially. Once downloaded, all playback works completely offline.'
              },
              {
                q: 'Can I use any YouTube video?',
                a: 'Only download videos you have the rights to, such as your own content. The responsibility for respecting copyright laws lies with you.'
              },
              {
                q: 'How do I update the app?',
                a: 'Download the latest APK from our GitHub releases page and install it over the existing app. Your settings and videos will be preserved.'
              },
              {
                q: 'Is my data safe?',
                a: 'Absolutely. We don\'t collect any analytics, tracking data, or personal information. All videos are stored locally on your device.'
              },
              {
                q: 'What Android versions are supported?',
                a: 'KidzTV works on most modern Android devices. Check the GitHub releases page for specific version requirements.'
              }
            ].map((faq, index) => (
              <div
                key={index}
                className="p-6 rounded-2xl border border-gray-100 hover:border-[#EC407A]/30 hover:shadow-lg transition-all duration-300"
              >
                <h3 className="text-lg font-bold mb-2 text-gray-900">{faq.q}</h3>
                <p className="text-gray-600 leading-relaxed">{faq.a}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <div className="relative overflow-hidden rounded-3xl">
            <div className="absolute inset-0 bg-gradient-to-r from-[#AB47BC] via-[#EC407A] to-[#EF5350]"></div>
            <div className="relative px-8 py-16 text-center">
              <h2 className="text-4xl sm:text-5xl font-bold text-white mb-6">
                Ready to Get Started?
              </h2>
              <p className="text-xl text-white/90 mb-8 max-w-2xl mx-auto">
                Download KidzTV today and give your children a safe, ad-free viewing experience.
              </p>
              <a
                href="https://github.com/eltonkola/kidztv/releases/latest/download/app-release-unsigned-signed.apk"
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-2 px-8 py-4 bg-white text-[#EC407A] rounded-full font-semibold hover:shadow-2xl hover:scale-105 transition-all duration-300"
              >
                <Download className="w-5 h-5" />
                Download Latest APK
              </a>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-12 px-4 sm:px-6 lg:px-8 bg-gray-900 text-gray-400">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row justify-between items-center gap-6">
            <div className="flex items-center gap-3">
              <img
                src="images/logo.png"
                alt="KidzTV Logo"
                className="h-10 w-auto"
                onError={(e) => {
                  e.currentTarget.style.display = 'none';
                  const fallback = e.currentTarget.nextElementSibling as HTMLElement;
                  if (fallback) fallback.style.display = 'flex';
                }}
              />
              <div className="hidden items-center gap-3">
                <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-[#AB47BC] via-[#EC407A] to-[#EF5350] flex items-center justify-center">
                  <Play className="w-6 h-6 text-white" fill="white" />
                </div>
                <span className="text-xl font-bold text-white">KidzTV</span>
              </div>
            </div>

            <div className="text-center md:text-left">
              <p className="text-sm">
                Copyright © 2019 Elton Kola. Licensed under the GNU GPL 3.0
              </p>
              <p className="text-sm mt-1">
                Open source and community-driven
              </p>
            </div>

            <div className="flex gap-4">
              <a
                href="https://github.com/eltonkola/kidztv"
                target="_blank"
                rel="noopener noreferrer"
                className="w-10 h-10 rounded-full bg-gray-800 hover:bg-gradient-to-br hover:from-[#AB47BC] hover:to-[#EF5350] flex items-center justify-center transition-all duration-300"
              >
                <Github className="w-5 h-5 text-white" />
              </a>
              <a
                href="https://github.com/eltonkola/kidztv/releases/latest/download/app-release-unsigned-signed.apk"
                target="_blank"
                rel="noopener noreferrer"
                className="w-10 h-10 rounded-full bg-gray-800 hover:bg-gradient-to-br hover:from-[#AB47BC] hover:to-[#EF5350] flex items-center justify-center transition-all duration-300"
              >
                <Download className="w-5 h-5 text-white" />
              </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;
