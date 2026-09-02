import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, Filter, Code } from 'lucide-react';
import { getProblems } from '../api/problems';
import Badge from '../components/common/Badge';
import { getDifficultyColor } from '../utils/scoreHelpers';
import LoadingSpinner from '../components/common/LoadingSpinner';

const ProblemsPage = () => {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  
  useEffect(() => {
    const fetchProbs = async () => {
      try {
        const response = await getProblems();
        setProblems(response.content || response);
      } catch (err) {
        // Mock data
        setProblems([
          { id: '1', title: 'Two Sum', difficulty: 'EASY', topic: 'Arrays', acceptanceRate: 48.5 },
          { id: '2', title: 'Add Two Numbers', difficulty: 'MEDIUM', topic: 'Linked Lists', acceptanceRate: 39.2 },
          { id: '3', title: 'Longest Substring Without Repeating Characters', difficulty: 'MEDIUM', topic: 'Strings', acceptanceRate: 33.8 },
          { id: '4', title: 'Median of Two Sorted Arrays', difficulty: 'HARD', topic: 'Arrays', acceptanceRate: 35.1 },
        ]);
      } finally {
        setLoading(false);
      }
    };
    fetchProbs();
  }, []);

  const filteredProblems = problems.filter(p => 
    p.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.topic.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <button className="px-4 py-2 bg-[#161b22] border border-[#30363d] hover:bg-[#30363d] text-white rounded-md transition-colors flex items-center text-sm font-medium whitespace-nowrap">
          <Filter className="w-4 h-4 mr-2" /> Filters
        </button>
      </div>

      {/* Problem List */}
      <div className="bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden">
        {loading ? (
          <LoadingSpinner />
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
                {filteredProblems.map((problem) => (
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
                {filteredProblems.length === 0 && (
                  <tr>
                    <td colSpan="4" className="px-6 py-12 text-center text-gray-500">
                      No problems found matching your criteria.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProblemsPage;
