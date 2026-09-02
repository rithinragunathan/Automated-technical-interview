import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Target, Award, Code, CheckCircle2, History } from 'lucide-react';
import { getDashboard as getDashboardData } from '../api/users';
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
      setError('Failed to load dashboard data.');
      // Mock data for development
      setData({
        stats: {
          overallScore: 82,
          interviewsCompleted: 12,
          problemsSolved: 45,
          successRate: 78
        },
        recentInterviews: [
          { id: 1, topic: 'Data Structures', type: 'CODING', status: 'COMPLETED', date: new Date().toISOString(), score: 85, duration: 45 },
          { id: 2, topic: 'System Design', type: 'TECHNICAL_QA', status: 'COMPLETED', date: new Date(Date.now() - 86400000 * 2).toISOString(), score: 72, duration: 60 }
        ],
        scoreTrend: [
          { date: '2023-10-01', score: 65, codingScore: 70 },
          { date: '2023-10-15', score: 72, codingScore: 75 },
          { date: '2023-11-01', score: 78, codingScore: 82 },
          { date: '2023-11-15', score: 85, codingScore: 88 }
        ]
      });
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
        <StatCard title="Overall Score" value={`${data?.stats?.overallScore || 0}%`} icon={Target} trend="up" trendValue="+5%" />
        <StatCard title="Interviews Completed" value={data?.stats?.interviewsCompleted || 0} icon={Award} />
        <StatCard title="Problems Solved" value={data?.stats?.problemsSolved || 0} icon={Code} />
        <StatCard title="Success Rate" value={`${data?.stats?.successRate || 0}%`} icon={CheckCircle2} trend="up" trendValue="+2%" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Charts Area */}
        <div className="lg:col-span-2 bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-semibold text-white">Score Trend</h2>
            <Link to="/analytics" className="text-sm text-brand-500 hover:text-brand-400">View Detailed Analytics</Link>
          </div>
          {data?.scoreTrend?.length > 0 ? (
            <ScoreTrendChart data={data.scoreTrend} />
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
            <h3 className="text-white font-medium mb-2">Practice Arrays & Hashing</h3>
            <p className="text-sm text-gray-400 mb-4">Your performance in this area was 60% in your last interview.</p>
            <Link to="/problems?topic=Arrays" className="px-4 py-2 bg-[#30363d] hover:bg-[#484f58] text-white rounded-md transition-colors text-sm w-full">
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
        
        {data?.recentInterviews?.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.recentInterviews.map((interview) => (
              <InterviewCard key={interview.id} interview={interview} />
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
