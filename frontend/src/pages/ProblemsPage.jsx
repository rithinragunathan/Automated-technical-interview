import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, Filter, Code, ChevronLeft, ChevronRight, RefreshCw } from 'lucide-react';
import { getProblems } from '../api/problems';
import Badge from '../components/common/Badge';
import { getDifficultyColor } from '../utils/scoreHelpers';
import { DIFFICULTIES } from '../utils/constants';
import LoadingSpinner from '../components/common/LoadingSpinner';

const PAGE_SIZE = 12;

const ProblemsPage = () => {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [difficulty, setDifficulty] = useState('ALL');
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalProblems, setTotalProblems] = useState(0);
  const [retryCount, setRetryCount] = useState(0);

  useEffect(() => {
    const timeout = setTimeout(() => setSearchQuery(searchTerm.trim()), 250);
    return () => clearTimeout(timeout);
  }, [searchTerm]);

  useEffect(() => {
    let isCurrentRequest = true;
    const fetchProblems = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await getProblems({
          page,
          size: PAGE_SIZE,
          difficulty: difficulty === 'ALL' ? undefined : difficulty,
          search: searchQuery || undefined
        });
        const items = Array.isArray(response) ? response : response?.content;
        if (!Array.isArray(items)) {
          throw new Error('The problems response was not a list.');
        }
        if (!isCurrentRequest) return;
        setProblems(items);
        setTotalPages(Array.isArray(response) ? (items.length ? 1 : 0) : response.totalPages || 0);
        setTotalProblems(Array.isArray(response) ? items.length : response.totalElements || 0);
      } catch (err) {
        console.error('Failed to load problems:', err);
        if (!isCurrentRequest) return;
        setError('Failed to load problems. Please try again.');
        setProblems([]);
      } finally {
        if (isCurrentRequest) setLoading(false);
      }
    };
    fetchProblems();
    return () => { isCurrentRequest = false; };
  }, [page, difficulty, searchQuery, retryCount]);

  const updateDifficulty = (value) => {
    setPage(0);
    setDifficulty(value);
  };

  const updateSearch = (value) => {
    setPage(0);
    setSearchTerm(value);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white mb-1">Problem Library</h1>
          <p className="text-sm text-gray-400">Practice coding questions across various topics and difficulties.</p>
        </div>
      </div>

      {/* Filters & Search */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="relative flex-grow">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-gray-500" />
          </div>
          <input
            type="text"
            className="block w-full pl-10 pr-3 py-2 bg-[#161b22] border border-[#30363d] rounded-md text-white placeholder-gray-500 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500 sm:text-sm"
            placeholder="Search problems by title or topic..."
            value={searchTerm}
            onChange={(e) => updateSearch(e.target.value)}
          />
        </div>
        <label className="px-3 py-2 bg-[#161b22] border border-[#30363d] text-gray-300 rounded-md flex items-center text-sm whitespace-nowrap">
          <Filter className="w-4 h-4 mr-2" />
          <span className="sr-only">Filter by difficulty</span>
          <select
            aria-label="Filter by difficulty"
            value={difficulty}
            onChange={event => updateDifficulty(event.target.value)}
            className="bg-transparent text-white focus:outline-none"
          >
            <option value="ALL">All difficulties</option>
            {DIFFICULTIES.map(({ id, name }) => <option key={id} value={id}>{name}</option>)}
          </select>
        </label>
      </div>

      {/* Problem List */}
      <div className="bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden">
        {loading ? (
          <LoadingSpinner />
        ) : error ? (
          <div className="flex items-center justify-center gap-3 p-8 text-sm text-red-400" role="alert">
            <span>{error}</span>
            <button
              type="button"
              onClick={() => setRetryCount(count => count + 1)}
              className="inline-flex items-center gap-1 text-white hover:text-brand-400"
            >
              <RefreshCw className="h-4 w-4" /> Retry
            </button>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-[#30363d]">
              <thead className="bg-[#0f1117]">
                <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Title</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Topic</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Difficulty</th>
                  <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-gray-400 uppercase tracking-wider">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#30363d]">
                {problems.map((problem) => (
                  <tr key={problem.id} className="hover:bg-[#30363d]/50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-white">{problem.title}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-300">{problem.topic}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <Badge variant={getDifficultyColor(problem.difficulty)} text={problem.difficulty} />
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <Link 
                        to={`/problems/${problem.id}`}
                        className="inline-flex items-center px-3 py-1.5 bg-[#30363d] hover:bg-brand-600 text-white rounded-md transition-colors"
                      >
                        <Code className="w-4 h-4 mr-1.5" /> Solve
                      </Link>
                    </td>
                  </tr>
                ))}
                {problems.length === 0 && (
                  <tr>
                    <td colSpan="4" className="px-6 py-12 text-center text-gray-500">
                      {totalProblems === 0 ? 'No problems are available yet.' : 'No problems found matching your criteria.'}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
      {!loading && !error && totalPages > 1 && (
        <div className="flex items-center justify-between text-sm text-gray-400">
          <span>
            Page {page + 1} of {totalPages} ({totalProblems} problems)
          </span>
          <div className="flex gap-2">
            <button
              type="button"
              onClick={() => setPage(currentPage => Math.max(0, currentPage - 1))}
              disabled={page === 0}
              className="inline-flex items-center rounded-md border border-[#30363d] px-3 py-2 text-white hover:bg-[#30363d] disabled:cursor-not-allowed disabled:opacity-50"
            >
              <ChevronLeft className="mr-1 h-4 w-4" /> Previous
            </button>
            <button
              type="button"
              onClick={() => setPage(currentPage => Math.min(totalPages - 1, currentPage + 1))}
              disabled={page >= totalPages - 1}
              className="inline-flex items-center rounded-md border border-[#30363d] px-3 py-2 text-white hover:bg-[#30363d] disabled:cursor-not-allowed disabled:opacity-50"
            >
              Next <ChevronRight className="ml-1 h-4 w-4" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProblemsPage;
