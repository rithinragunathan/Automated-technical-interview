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
      console.error('Analytics error:', err);
      setError('Failed to load analytics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const scoreHistory = data?.scoreHistory || [];
  const scores = scoreHistory.map((item) => item.overallScore);
  const averageScore = scores.length
    ? scores.reduce((total, score) => total + score, 0) / scores.length
    : 0;
  const bestScore = scores.length ? Math.max(...scores) : 0;
  const topicData = Object.entries(data?.topicPerformance || {}).map(([topic, score]) => ({
    topic,
    score,
  }));
  const difficultyData = Object.entries(data?.difficultyPerformance || {}).map(([difficulty, score]) => ({
    difficulty,
    score,
  }));
  const latestScore = scoreHistory[scoreHistory.length - 1];
  const radarData = latestScore
    ? [
        { subject: 'Overall', score: latestScore.overallScore, fullMark: 100 },
        { subject: 'Coding', score: latestScore.codingScore, fullMark: 100 },
        { subject: 'Knowledge', score: latestScore.knowledgeScore, fullMark: 100 },
      ]
    : undefined;

  if (loading) return <LoadingSpinner message="Loading analytics..." />;
  if (error && !data) return <ErrorMessage message="Failed to load analytics." />;

  return (
    <div className="space-y-6 pb-12">
      <div>
        <h1 className="text-2xl font-bold text-white mb-2">Performance Analytics</h1>
        <p className="text-gray-400">Detailed insights into your interview performance and skill progression.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Interviews" value={data?.totalInterviews ?? 0} icon={BookOpen} />
        <StatCard title="Average Score" value={`${averageScore.toFixed(1)}%`} icon={Target} />
        <StatCard title="Best Score" value={`${bestScore.toFixed(1)}%`} icon={Award} trend="up" />
        <StatCard title="Problems Attempted" value={data?.totalProblems ?? 0} icon={Code} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4">Score Trend Over Time</h2>
          <ScoreTrendChart data={scoreHistory} />
        </div>
        
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4">Skill Dimension Breakdown</h2>
          <ScoreRadarChart data={radarData} />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4">Topic Performance</h2>
          <TopicPerformanceChart data={topicData} />
        </div>
        
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5">
          <h2 className="text-lg font-semibold text-white mb-4">Difficulty Success Rate</h2>
          <DifficultyPerformanceChart data={difficultyData} />
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
