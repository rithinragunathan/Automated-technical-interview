import React, { useState, useEffect } from 'react';
import { getDashboard } from '../api/analytics';
import ScoreTrendChart from '../components/analytics/ScoreTrendChart';
import TopicPerformanceChart from '../components/analytics/TopicPerformanceChart';
import DifficultyPerformanceChart from '../components/analytics/DifficultyPerformanceChart';
import ScoreRadarChart from '../components/analytics/ScoreRadarChart';
import StatCard from '../components/common/StatCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import Badge from '../components/common/Badge';
import { Target, Award, Code, BookOpen, AlertCircle } from 'lucide-react';

const AnalyticsPage = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchAnalytics = async () => {
    try {
      const response = await getDashboard();
      setData(response);
    } catch (err) {
      // Mock data
      setData({
        stats: { totalInterviews: 15, avgScore: 76, bestScore: 92, problemsSolved: 48 },
        trendData: [
          { date: '2023-09-01', score: 60, codingScore: 65, knowledgeScore: 55 },
          { date: '2023-09-15', score: 68, codingScore: 70, knowledgeScore: 65 },
          { date: '2023-10-01', score: 75, codingScore: 80, knowledgeScore: 70 },
          { date: '2023-10-15', score: 72, codingScore: 75, knowledgeScore: 68 },
          { date: '2023-11-01', score: 85, codingScore: 88, knowledgeScore: 82 }
        ],
        topicData: [
          { topic: 'Arrays', score: 85 },
          { topic: 'Strings', score: 90 },
          { topic: 'Graphs', score: 45 },
          { topic: 'System Design', score: 60 },
          { topic: 'Database', score: 75 }
        ],
        difficultyData: [
          { difficulty: 'Easy', attempted: 20, solved: 18 },
          { difficulty: 'Medium', attempted: 25, solved: 15 },
          { difficulty: 'Hard', attempted: 10, solved: 2 }
        ],
        weakestConcepts: ['Dynamic Programming', 'Graph Traversal', 'System Scalability'],
        strongestConcepts: ['Array Manipulation', 'String Formatting', 'Basic SQL']
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnalytics();
  }, []);

  if (loading) return <LoadingSpinner message="Loading analytics..." />;
  if (error && !data) return <ErrorMessage message="Failed to load analytics." />;

  return (
    <div className="space-y-6 pb-12">
      <div>
        <h1 className="text-2xl font-bold text-white mb-2">Performance Analytics</h1>
        <p className="text-gray-400">Detailed insights into your interview performance and skill progression.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Interviews" value={data?.stats?.totalInterviews} icon={BookOpen} />
        <StatCard title="Average Score" value={`${data?.stats?.avgScore}%`} icon={Target} />
        <StatCard title="Best Score" value={`${data?.stats?.bestScore}%`} icon={Award} trend="up" trendValue="Top 10%" />
        <StatCard title="Problems Solved" value={data?.stats?.problemsSolved} icon={Code} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4">Score Trend Over Time</h2>
          {data?.trendData && <ScoreTrendChart data={data.trendData} />}
        </div>
        
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4">Skill Dimension Breakdown</h2>
          <ScoreRadarChart />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4">Topic Performance</h2>
          {data?.topicData && <TopicPerformanceChart data={data.topicData} />}
        </div>
        
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4">Difficulty Success Rate</h2>
          {data?.difficultyData && <DifficultyPerformanceChart data={data.difficultyData} />}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4 flex items-center">
            <Award className="w-5 h-5 text-green-500 mr-2" /> Strongest Concepts
          </h2>
          <div className="flex flex-wrap gap-2">
            {data?.strongestConcepts?.map((concept, idx) => (
              <Badge key={idx} variant="success" text={concept} />
            ))}
          </div>
        </div>
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4 flex items-center">
            <AlertCircle className="w-5 h-5 text-red-500 mr-2" /> Areas to Improve
          </h2>
          <div className="flex flex-wrap gap-2">
            {data?.weakestConcepts?.map((concept, idx) => (
              <Badge key={idx} variant="error" text={concept} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnalyticsPage;
