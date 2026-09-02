import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getInterviews } from '../api/interviews';
import { formatDate } from '../utils/formatDate';
import { getStatusColor, getScoreColor, getDifficultyColor } from '../utils/scoreHelpers';
import Badge from '../components/common/Badge';
import LoadingSpinner from '../components/common/LoadingSpinner';
import InterviewCard from '../components/interview/InterviewCard';
import { Filter, Search, ChevronRight } from 'lucide-react';

const InterviewHistoryPage = () => {
  const [interviews, setInterviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [view, setView] = useState('table'); // table or grid
  const navigate = useNavigate();

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const data = await getInterviews();
        setInterviews(data.content || data);
      } catch (err) {
        // Mock data
        setInterviews([
          { id: 1, topic: 'Data Structures', type: 'CODING', difficulty: 'MEDIUM', status: 'COMPLETED', date: new Date().toISOString(), score: 85, durationMinutes: 45 },
          { id: 2, topic: 'System Design', type: 'TECHNICAL_QA', difficulty: 'HARD', status: 'COMPLETED', date: new Date(Date.now() - 86400000 * 2).toISOString(), score: 72, durationMinutes: 60 },
          { id: 3, topic: 'Algorithms', type: 'CODING', difficulty: 'EASY', status: 'IN_PROGRESS', date: new Date(Date.now() - 86400000 * 5).toISOString(), score: null, durationMinutes: 30 }
        ]);
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);

  if (loading) return <LoadingSpinner message="Loading history..." />;

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white mb-2">Interview History</h1>
          <p className="text-gray-400">Review your past mock interviews and their reports.</p>
        </div>
        <Link 
          to="/interviews/new"
          className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-md transition-colors text-sm font-medium"
        >
          Start New Interview
        </Link>
      </div>

      <div className="flex flex-col sm:flex-row gap-4 mb-4">
        <div className="relative flex-grow">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-gray-500" />
          </div>
          <input
            type="text"
            className="block w-full pl-10 pr-3 py-2 bg-[#161b22] border border-[#30363d] rounded-md text-white placeholder-gray-500 focus:outline-none focus:border-brand-500 sm:text-sm"
            placeholder="Search by topic..."
          />
        </div>
        <div className="flex space-x-2">
          <select className="bg-[#161b22] border border-[#30363d] text-white text-sm rounded-md px-3 py-2 focus:outline-none focus:border-brand-500">
            <option>All Types</option>
            <option>Coding</option>
            <option>Technical Q&A</option>
          </select>
          <select className="bg-[#161b22] border border-[#30363d] text-white text-sm rounded-md px-3 py-2 focus:outline-none focus:border-brand-500">
            <option>All Statuses</option>
            <option>Completed</option>
            <option>In Progress</option>
          </select>
        </div>
      </div>

      {/* Mobile view uses Cards always, Desktop uses Table */}
      <div className="block md:hidden grid grid-cols-1 gap-4">
        {interviews.map(interview => (
          <InterviewCard key={interview.id} interview={interview} />
        ))}
      </div>

      <div className="hidden md:block bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden">
        <table className="min-w-full divide-y divide-[#30363d]">
          <thead className="bg-[#0f1117]">
            <tr>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Date & Topic</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Type / Difficulty</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Status</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Score</th>
              <th scope="col" className="relative px-6 py-3"><span className="sr-only">Actions</span></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#30363d]">
            {interviews.map((interview) => (
              <tr 
                key={interview.id} 
                className="hover:bg-[#30363d]/50 transition-colors cursor-pointer"
                onClick={() => navigate(`/interviews/${interview.id}/${interview.status === 'COMPLETED' ? 'report' : 'live'}`)}
              >
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm font-medium text-white">{interview.topic}</div>
                  <div className="text-sm text-gray-500">{formatDate(interview.date)}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm text-gray-300">{interview.type.replace('_', ' ')}</div>
                  <Badge variant={getDifficultyColor(interview.difficulty)} text={interview.difficulty} className="mt-1" />
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <Badge variant={getStatusColor(interview.status)} text={interview.status} />
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {interview.score !== null ? (
                    <span className="font-semibold" style={{ color: getScoreColor(interview.score) }}>
                      {interview.score}%
                    </span>
                  ) : (
                    <span className="text-gray-500">N/A</span>
                  )}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <ChevronRight className="w-5 h-5 text-gray-400 inline-block" />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default InterviewHistoryPage;
