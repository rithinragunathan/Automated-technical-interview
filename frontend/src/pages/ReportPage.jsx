import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Download, Share2, ArrowLeft, CheckCircle2, AlertTriangle, Lightbulb } from 'lucide-react';
import { getReport } from '../api/interviews';
import ScoreGauge from '../components/common/ScoreGauge';
import ProgressBar from '../components/common/ProgressBar';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import Badge from '../components/common/Badge';

const ReportPage = () => {
  const { id } = useParams();
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [retryCount, setRetryCount] = useState(0);
  const overallScore = report?.score?.overallScore ?? report?.overallScore ?? 0;
  const scoreBreakdown = report?.codingBreakdown ? {
    correctness: report.codingBreakdown.correctnessScore,
    timeEfficiency: report.codingBreakdown.timeEfficiencyScore,
    spaceEfficiency: report.codingBreakdown.spaceEfficiencyScore,
    codeQuality: report.codingBreakdown.codeQualityScore,
    robustness: report.codingBreakdown.robustnessScore,
    readability: report.codingBreakdown.readabilityScore
  } : (report?.scoreBreakdown || {});

  useEffect(() => {
    const fetchReport = async () => {
      try {
        const data = await getReport(id);
        setReport(data);
      } catch (err) {
        console.error('Failed to load report:', err);
        setError(err.response?.data?.message || 'Failed to load report.');
      } finally {
        setLoading(false);
      }
    };
    setLoading(true);
    setError(null);
    fetchReport();
  }, [id, retryCount]);

  if (loading) return <LoadingSpinner message="Generating report..." />;
  if (error && !report) {
    return <ErrorMessage message={error} onRetry={() => setRetryCount(count => count + 1)} />;
  }

  return (
    <div className="max-w-5xl mx-auto space-y-8 pb-12">
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <Link to="/interviews" className="text-gray-400 hover:text-white flex items-center text-sm mb-2 transition-colors">
            <ArrowLeft className="w-4 h-4 mr-1" /> Back to History
          </Link>
          <h1 className="text-2xl font-bold text-white">Interview Performance Report</h1>
          <p className="text-gray-400 mt-1">Topic: {report.topic} | Type: {report.interviewType}</p>
        </div>
        <div className="flex space-x-3">
          <button className="px-4 py-2 bg-[#161b22] border border-[#30363d] hover:bg-[#30363d] text-white rounded-md transition-colors flex items-center text-sm font-medium">
            <Share2 className="w-4 h-4 mr-2" /> Share
          </button>
          <button className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-md transition-colors flex items-center text-sm font-medium">
            <Download className="w-4 h-4 mr-2" /> Download PDF
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Col: Overall Score & Breakdown */}
        <div className="lg:col-span-1 space-y-6">
          <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-6 flex flex-col items-center justify-center">
            <h2 className="text-lg font-semibold text-white mb-6 w-full text-center">Overall Score</h2>
            <ScoreGauge score={overallScore} size={160} strokeWidth={12} label="out of 100" />
            <div className="mt-6 text-center">
              <Badge 
                variant={overallScore >= 80 ? 'success' : overallScore >= 60 ? 'warning' : 'error'}
                text={overallScore >= 80 ? 'Strong Hire' : overallScore >= 60 ? 'Leaning Hire' : 'No Hire'}
                className="text-sm px-3 py-1"
              />
            </div>
          </div>

          <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-6">
            <h2 className="text-lg font-semibold text-white mb-6">Score Breakdown</h2>
            <div className="space-y-5">
              {Object.entries(scoreBreakdown).map(([category, score]) => (
                <div key={category}>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-300 capitalize">{category.replace(/([A-Z])/g, ' $1').trim()}</span>
                    <span className="font-medium text-white">{score}%</span>
                  </div>
                  <ProgressBar 
                    value={score} 
                    color={score >= 80 ? 'bg-green-500' : score >= 60 ? 'bg-yellow-500' : 'bg-red-500'} 
                  />
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Col: Feedback Details */}
        <div className="lg:col-span-2 space-y-6">
          
          {/* Strengths */}
          <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-6">
            <h2 className="text-lg font-semibold text-white mb-4 flex items-center">
              <CheckCircle2 className="w-5 h-5 text-green-500 mr-2" />
              Key Strengths
            </h2>
            <ul className="space-y-3">
              {report.strengths.map((item, idx) => (
                <li key={idx} className="flex items-start bg-green-900/10 border border-green-900/20 p-3 rounded-md">
                  <span className="h-1.5 w-1.5 rounded-full bg-green-500 mt-2 mr-3 flex-shrink-0"></span>
                  <span className="text-gray-300 text-sm leading-relaxed">{item}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Areas for Improvement */}
          <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-6">
            <h2 className="text-lg font-semibold text-white mb-4 flex items-center">
              <AlertTriangle className="w-5 h-5 text-yellow-500 mr-2" />
              Areas for Improvement
            </h2>
            <ul className="space-y-3">
              {report.weaknesses.map((item, idx) => (
                <li key={idx} className="flex items-start bg-yellow-900/10 border border-yellow-900/20 p-3 rounded-md">
                  <span className="h-1.5 w-1.5 rounded-full bg-yellow-500 mt-2 mr-3 flex-shrink-0"></span>
                  <span className="text-gray-300 text-sm leading-relaxed">{item}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Actionable Recommendations */}
          <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-6">
            <h2 className="text-lg font-semibold text-white mb-4 flex items-center">
              <Lightbulb className="w-5 h-5 text-brand-400 mr-2" />
              Recommended Actions
            </h2>
            <ul className="space-y-3">
              {(report.recommendations || []).map((item, idx) => (
                <li key={idx} className="flex items-start bg-brand-900/10 border border-brand-900/20 p-3 rounded-md">
                  <span className="h-1.5 w-1.5 rounded-full bg-brand-500 mt-2 mr-3 flex-shrink-0"></span>
                  <span className="text-gray-300 text-sm leading-relaxed">{typeof item === 'string' ? item : item.title || item.description}</span>
                </li>
              ))}
            </ul>
          </div>

        </div>
      </div>
    </div>
  );
};

export default ReportPage;
