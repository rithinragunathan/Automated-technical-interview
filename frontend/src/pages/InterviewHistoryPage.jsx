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
  const [searchTerm, setSearchTerm] = useState('');
  const [typeFilter, setTypeFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const data = await getInterviews();
        const items = Array.isArray(data) ? data : data?.content;
        if (!Array.isArray(items)) {
          throw new Error('The interviews response was not a list.');
        }
        setInterviews(items);
      } catch (err) {
        console.error('Failed to load interviews:', err);
        setError('Failed to load interview history. Please try again.');
        setInterviews([]);
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);

  const filteredInterviews = interviews.filter(interview => {
    const type = interview.interviewType || interview.type || '';
    const matchesSearch = (interview.topic || '').toLowerCase().includes(searchTerm.toLowerCase());
    return matchesSearch &&
      (typeFilter === 'ALL' || type === typeFilter) &&
      (statusFilter === 'ALL' || interview.status === statusFilter);
  });

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
            value={searchTerm}
            onChange={event => setSearchTerm(event.target.value)}
          />
        </div>
        <div className="flex space-x-2">
          <select aria-label="Filter by interview type" value={typeFilter} onChange={event => setTypeFilter(event.target.value)} className="bg-[#161b22] border border-[#30363d] text-white text-sm rounded-md px-3 py-2 focus:outline-none focus:border-brand-500">
            <option value="ALL">All Types</option>
            <option value="CODING">Coding</option>
            <option value="TECHNICAL_QA">Technical Q&amp;A</option>
            <option value="MIXED">Mixed</option>
          </select>
          <select aria-label="Filter by interview status" value={statusFilter} onChange={event => setStatusFilter(event.target.value)} className="bg-[#161b22] border border-[#30363d] text-white text-sm rounded-md px-3 py-2 focus:outline-none focus:border-brand-500">
            <option value="ALL">All Statuses</option>
            <option value="COMPLETED">Completed</option>
            <option value="ACTIVE">In Progress</option>
            <option value="PENDING">Not Started</option>
          </select>
        </div>
      </div>

      {/* Mobile view uses Cards always, Desktop uses Table */}
      <div className="block md:hidden grid grid-cols-1 gap-4">
        {filteredInterviews.map(interview => (
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
            {filteredInterviews.map((interview) => (
              <tr 
                key={interview.id} 
                className="hover:bg-[#30363d]/50 transition-colors cursor-pointer"
                onClick={() => navigate(
                  interview.status === 'COMPLETED'
                    ? `/interviews/${interview.id}/report`
                    : interview.status === 'PENDING'
                      ? `/interviews/${interview.id}/instructions`
                      : `/interviews/${interview.id}/live`
                )}
              >
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm font-medium text-white">{interview.topic}</div>
                  <div className="text-sm text-gray-500">{formatDate(interview.createdAt || interview.date)}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm text-gray-300">{(interview.interviewType || interview.type || '').replaceAll('_', ' ')}</div>
                  <Badge variant={getDifficultyColor(interview.difficulty)} text={interview.difficulty} className="mt-1" />
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <Badge variant={getStatusColor(interview.status)} text={interview.status} />
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {(interview.overallScore ?? interview.score) !== null && (interview.overallScore ?? interview.score) !== undefined ? (
                    <span className="font-semibold" style={{ color: getScoreColor(interview.overallScore ?? interview.score) }}>
                      {interview.overallScore ?? interview.score}%
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
            {!error && filteredInterviews.length === 0 && (
              <tr>
                <td colSpan="5" className="px-6 py-12 text-center text-gray-500">
                  {interviews.length === 0 ? 'You have no interviews yet.' : 'No interviews match your filters.'}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      {error && <p role="alert" className="text-sm text-red-400">{error}</p>}
    </div>
  );
};

export default InterviewHistoryPage;
