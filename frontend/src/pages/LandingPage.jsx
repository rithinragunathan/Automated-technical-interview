import React from 'react';
import { Link, Navigate } from 'react-router-dom';
import { Brain, Code, LineChart, MessageSquare, ChevronRight, CheckCircle } from 'lucide-react';
import useAuth from '../hooks/useAuth';

const LandingPage = () => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div className="min-h-screen bg-[#0f1117]"></div>;
  }

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const features = [
    {
      title: 'Real-time Coding Environment',
      description: 'Practice in a professional IDE with multi-language support, auto-completion, and instant test case validation.',
      icon: Code,
    },
    {
      title: 'AI-Powered Interviewer',
      description: 'Engage in natural, dynamic conversations. The AI adapts questions based on your previous answers and skill level.',
      icon: MessageSquare,
    },
    {
      title: 'Detailed Analytics',
      description: 'Track your progress over time with comprehensive metrics on coding efficiency, communication skills, and topic mastery.',
      icon: LineChart,
    },
    {
      title: 'Actionable Feedback',
      description: 'Receive instant, constructive feedback on your code quality, complexity, and behavioral responses.',
      icon: CheckCircle,
    },
  ];

  return (
    <div className="min-h-screen bg-[#0f1117] text-[#e6edf3] font-sans selection:bg-brand-500/30">
      {/* Navbar */}
      <nav className="flex items-center justify-between px-6 py-4 md:px-12 lg:px-24 border-b border-[#30363d] bg-[#0f1117]/80 backdrop-blur-md sticky top-0 z-50">
        <div className="flex items-center">
          <Brain className="w-8 h-8 text-brand-500 mr-2" />
          <span className="text-xl font-bold tracking-tight">Mock<span className="text-brand-500">IQ</span></span>
        </div>
        <div className="space-x-4">
          <Link to="/login" className="text-sm font-medium text-gray-300 hover:text-white transition-colors">
            Log in
          </Link>
          <Link to="/register" className="px-4 py-2 text-sm font-medium bg-brand-600 hover:bg-brand-500 text-white rounded-md transition-colors">
            Sign up
          </Link>
        </div>
      </nav>

      {/* Hero Section */}
      <main className="flex flex-col items-center justify-center text-center px-6 py-20 md:py-32 max-w-5xl mx-auto">
        <Badge />
        <h1 className="text-5xl md:text-7xl font-extrabold tracking-tight mb-6 bg-gradient-to-r from-white via-gray-200 to-gray-500 bg-clip-text text-transparent">
          Master Your Next <br />Technical Interview
        </h1>
        <p className="text-lg md:text-xl text-gray-400 max-w-2xl mb-10 leading-relaxed">
          Practice coding and system design with an adaptive AI interviewer. Get real-time feedback, detailed performance analytics, and tailored recommendations to land your dream job.
        </p>
        <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4">
          <Link to="/register" className="px-8 py-4 text-base font-semibold bg-brand-600 hover:bg-brand-500 text-white rounded-lg transition-all hover:shadow-lg hover:shadow-brand-500/20 flex items-center justify-center">
            Start Practicing Free
            <ChevronRight className="w-5 h-5 ml-2" />
          </Link>
          <a href="#features" className="px-8 py-4 text-base font-semibold bg-[#161b22] hover:bg-[#30363d] border border-[#30363d] text-white rounded-lg transition-colors flex items-center justify-center">
            Explore Features
          </a>
        </div>
      </main>

      {/* Features Section */}
      <section id="features" className="py-24 bg-[#0a0c10] border-t border-[#30363d]">
        <div className="max-w-7xl mx-auto px-6 md:px-12">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">Everything you need to succeed</h2>
            <p className="text-gray-400 max-w-2xl mx-auto">Comprehensive tools designed to simulate real interview environments and accelerate your learning process.</p>
          </div>
          
          <div className="grid md:grid-cols-2 gap-8">
            {features.map((feature, idx) => (
              <div key={idx} className="bg-[#161b22] border border-[#30363d] rounded-xl p-8 hover:border-brand-500/50 transition-colors group">
                <div className="w-12 h-12 bg-brand-900/30 rounded-lg flex items-center justify-center mb-6 border border-brand-500/20 group-hover:bg-brand-600 transition-colors">
                  <feature.icon className="w-6 h-6 text-brand-400 group-hover:text-white" />
                </div>
                <h3 className="text-xl font-bold mb-3">{feature.title}</h3>
                <p className="text-gray-400 leading-relaxed">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-[#0f1117] border-t border-[#30363d] py-12 text-center text-gray-500 text-sm">
        <div className="flex items-center justify-center mb-4">
          <Brain className="w-6 h-6 text-gray-600 mr-2" />
          <span className="text-lg font-bold tracking-tight text-gray-400">MockIQ</span>
        </div>
        <p>&copy; {new Date().getFullYear()} MockIQ Sandbox. All rights reserved.</p>
      </footer>
    </div>
  );
};

const Badge = () => (
  <div className="inline-flex items-center px-3 py-1 rounded-full bg-brand-900/30 border border-brand-500/30 text-brand-400 text-sm font-medium mb-8">
    <span className="flex h-2 w-2 rounded-full bg-brand-500 mr-2 animate-pulse"></span>
    Now with AI-powered System Design interviews
  </div>
);

export default LandingPage;
