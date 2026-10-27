import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Target, Award, Code, CheckCircle2, History } from 'lucide-react';
import { getDashboard as getDashboardData } from '../api/analytics';
import StatCard from '../components/common/StatCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import ScoreTrendChart from '../components/analytics/ScoreTrendChart';
import InterviewCard from '../components/interview/InterviewCard';
import Badge from '../components/common/Badge';

const DashboardPage = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboard = async () => {
    setLoading(true);
    try {
      const response = await getDashboardData();
      setData(response);
      setError(null);
    } catch (err) {
      console.error('Dashboard error:', err);
      setError('Failed to load dashboard data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  if (loading) return <LoadingSpinner message="Loading dashboard..." />;
  if (error && !data) return <ErrorMessage message={error} onRetry={fetchDashboard} />;

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h1 className="text-2xl font-bold text-white">Dashboard</h1>
        <Link 
          to="/interviews/new"
          className="flex items-center px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-md transition-colors font-medium text-sm"
        >
          <Plus className="w-4 h-4 mr-2" />
          New Interview
        </Link>
      </div>

      {/* Stats Row */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Average Score" value={`${data?.scoreHistory?.length ? (data.scoreHistory.reduce((sum, item) => sum + item.overallScore, 0) / data.scoreHistory.length).toFixed(1) : 0}%`} icon={Target} />
        <StatCard title="Interviews Completed" value={data?.totalInterviews || 0} icon={Award} />
        <StatCard title="Problems Attempted" value={data?.totalProblems || 0} icon={Code} />
        <StatCard title="Success Rate" value={`${(data?.successRate || 0).toFixed(1)}%`} icon={CheckCircle2} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Charts Area */}
        <div className="lg:col-span-2 bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-semibold text-white">Score Trend</h2>
            <Link to="/analytics" className="text-sm text-brand-500 hover:text-brand-400">View Detailed Analytics</Link>
          </div>
          {data?.scoreHistory?.length > 0 ? (
            <ScoreTrendChart data={data.scoreHistory} />
          ) : (
            <div className="h-72 flex items-center justify-center text-gray-500">Not enough data to show trend</div>
          )}
        </div>

        {/* Action / Next Steps Area */}
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5 flex flex-col">
          <h2 className="text-lg font-semibold text-white mb-4">Suggested Next Step</h2>
          
          <div className="flex-grow flex flex-col justify-center items-center text-center p-6 border border-dashed border-[#484f58] rounded-lg bg-[#0f1117]/50">
            <div className="w-12 h-12 bg-brand-900/30 rounded-full flex items-center justify-center mb-4 text-brand-500">
              <Code className="w-6 h-6" />
            </div>
            <h3 className="text-white font-medium mb-2">{data?.weakestConcepts?.[0] || 'Start with a coding problem'}</h3>
            <p className="text-sm text-gray-400 mb-4">Practice a topic from your current performance data.</p>
            <Link to={`/problems${data?.weakestConcepts?.[0] ? `?topic=${encodeURIComponent(data.weakestConcepts[0])}` : ''}`} className="px-4 py-2 bg-[#30363d] hover:bg-[#484f58] text-white rounded-md transition-colors text-sm w-full">
              Solve Problems
            </Link>
          </div>
        </div>
      </div>

      {/* Recent Interviews */}
      <div>
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold text-white flex items-center">
            <History className="w-5 h-5 mr-2 text-gray-400" />
            Recent Interviews
          </h2>
          <Link to="/interviews" className="text-sm text-gray-400 hover:text-white">View All</Link>
        </div>
        
        {data?.scoreHistory?.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.scoreHistory.slice(-3).reverse().map((score) => (
              <div key={score.interviewId} className="bg-[#161b22] border border-[#30363d] rounded-lg p-4">
                <p className="text-sm text-gray-400">Interview #{score.interviewId}</p>
                <p className="text-2xl font-semibold text-white mt-2">{score.overallScore}%</p>
                <p className="text-xs text-gray-500 mt-1">{score.date}</p>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-8 text-center">
            <p className="text-gray-400 mb-4">You haven't taken any mock interviews yet.</p>
            <Link to="/interviews/new" className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-md transition-colors inline-block">
              Start Your First Interview
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;
