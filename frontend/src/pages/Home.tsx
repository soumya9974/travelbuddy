import { ArrowRight, Globe, Users, Map, Calendar } from "lucide-react";
import { Button } from "../components/ui/Button";

interface HomeProps {
  onGetStarted: () => void;
}

export const Home = ({ onGetStarted }: HomeProps) => {
  return (
    <div className="min-h-screen">
      <div className="relative h-screen">
        <div
          className="absolute inset-0 bg-cover bg-center bg-no-repeat"
          style={{
            backgroundImage:
              "url('https://images.pexels.com/photos/1008155/pexels-photo-1008155.jpeg?auto=compress&cs=tinysrgb&w=1920')",
          }}
        >
          <div className="absolute inset-0 bg-gradient-to-b from-black/60 via-black/40 to-black/70" />
        </div>

        <div className="relative z-10 h-full flex items-center justify-center px-4">
          <div className="max-w-4xl mx-auto text-center text-white">
            <h1 className="text-5xl md:text-7xl font-bold mb-6 leading-tight">
              Discover the World
              <span className="block text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-cyan-300">
                Together
              </span>
            </h1>
            <p className="text-xl md:text-2xl mb-8 text-gray-200 font-light leading-relaxed">
              "The world is a book, and those who do not travel read only one
              page."
            </p>
            <p className="text-lg text-gray-300 mb-12">
              Connect with fellow travelers, plan unforgettable adventures, and
              create memories that last a lifetime.
            </p>
            <Button
              onClick={onGetStarted}
              size="lg"
              className="text-lg px-8 py-6 bg-blue-600 hover:bg-blue-700 text-white shadow-2xl transform hover:scale-105 transition-all duration-300"
            >
              Start Your Journey
              <ArrowRight className="ml-2" size={24} />
            </Button>
          </div>
        </div>

        <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-white via-white to-transparent h-32" />
      </div>

      <div className="bg-white py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
              Why Travel Together?
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Join a community of passionate travelers and make your adventures
              more memorable
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="text-center p-6 rounded-xl hover:shadow-lg transition-shadow">
              <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Users className="text-blue-600" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Find Travel Buddies
              </h3>
              <p className="text-gray-600">
                Connect with like-minded travelers who share your passion for
                exploration
              </p>
            </div>

            <div className="text-center p-6 rounded-xl hover:shadow-lg transition-shadow">
              <div className="bg-cyan-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Map className="text-cyan-600" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Plan Together
              </h3>
              <p className="text-gray-600">
                Collaborate on itineraries and create the perfect travel plan as
                a group
              </p>
            </div>

            <div className="text-center p-6 rounded-xl hover:shadow-lg transition-shadow">
              <div className="bg-teal-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Calendar className="text-teal-600" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Stay Organized
              </h3>
              <p className="text-gray-600">
                Keep track of your travel plans, schedules, and important
                details in one place
              </p>
            </div>

            <div className="text-center p-6 rounded-xl hover:shadow-lg transition-shadow">
              <div className="bg-emerald-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Globe className="text-emerald-600" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Explore More
              </h3>
              <p className="text-gray-600">
                Discover new destinations and hidden gems recommended by fellow
                travelers
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-gradient-to-br from-blue-50 via-cyan-50 to-teal-50 py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-8">
              Words That Inspire
            </h2>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-shadow">
              <div className="text-6xl text-blue-600 mb-4">&ldquo;</div>
              <p className="text-lg text-gray-700 mb-6 italic leading-relaxed">
                Travel is the only thing you buy that makes you richer.
              </p>
              <p className="text-sm text-gray-500 font-medium">Anonymous</p>
            </div>

            <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-shadow">
              <div className="text-6xl text-cyan-600 mb-4">&ldquo;</div>
              <p className="text-lg text-gray-700 mb-6 italic leading-relaxed">
                We travel not to escape life, but for life not to escape us.
              </p>
              <p className="text-sm text-gray-500 font-medium">Anonymous</p>
            </div>

            <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-shadow">
              <div className="text-6xl text-teal-600 mb-4">&ldquo;</div>
              <p className="text-lg text-gray-700 mb-6 italic leading-relaxed">
                Adventure is worthwhile in itself.
              </p>
              <p className="text-sm text-gray-500 font-medium">
                Amelia Earhart
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-gradient-to-r from-blue-600 to-cyan-600 py-20">
        <div className="max-w-4xl mx-auto text-center px-4">
          <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
            Ready to Begin Your Adventure?
          </h2>
          <p className="text-xl text-blue-100 mb-10">
            Join thousands of travelers who have already started their journey
            with us
          </p>
          <Button
            onClick={onGetStarted}
            size="lg"
            className="text-lg px-8 py-6 bg-white !text-blue-600 hover:bg-gray-50 shadow-2xl transform hover:scale-105 transition-all duration-300"
          >
            Explore Travel Groups
            <ArrowRight className="ml-2" size={24} />
          </Button>
        </div>
      </div>
    </div>
  );
};
