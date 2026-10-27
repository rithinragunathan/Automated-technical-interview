import React, { useState, useEffect } from 'react';
import { getRecommendations, updateRecommendationCompletion } from '../api/recommendations';
import { CheckCircle2, Lightbulb, BookOpen, ExternalLink, Filter } from 'lucide-react';
import Badge from '../components/common/Badge';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import ErrorMessage from '../components/common/ErrorMessage';
import toast from 'react-hot-toast';

const RecommendationsPage = () => {
  const [recs, setRecs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('All');
  const [error, setError] = useState(null);
  const [savingIds, setSavingIds] = useState([]);

  const fetchRecs = async () => {
      try {
        const data = await getRecommendations();
        if (!Array.isArray(data)) {
          throw new Error('The recommendations response was not a list.');
        }
        setRecs(data);
        setError(null);
      } catch (err) {
        console.error('Recommendations error:', err);
        setError('Failed to load recommendations. Please try again.');
        setRecs([]);
      } finally {
        setLoading(false);
      }
  };

  useEffect(() => {
    fetchRecs();
  }, []);

  const categories = ['All', ...new Set(recs.map(r => r.category))];
  const filteredRecs = filter === 'All' ? recs : recs.filter(r => r.category === filter);

  const toggleComplete = async (recommendation) => {
    const completed = !recommendation.completed;
    setSavingIds(ids => [...ids, recommendation.id]);
    try {
      const updated = await updateRecommendationCompletion(recommendation.id, completed);
      setRecs(current => current.map(item => item.id === updated.id ? updated : item));
    } catch (err) {
      console.error('Failed to update recommendation:', err);
      toast.error('Could not update recommendation. Please try again.');
    } finally {
      setSavingIds(ids => ids.filter(id => id !== recommendation.id));
    }
  };

  const getPriorityColor = (priority) => {
    switch(priority) {
      case 1: return 'error';
      case 2: return 'warning';
      case 3: return 'info';
      default: return 'neutral';
    }
  };

  if (loading) return <LoadingSpinner message="Loading recommendations..." />;
  if (error) return <ErrorMessage message={error} onRetry={() => { setLoading(true); fetchRecs(); }} />;

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white mb-2">Personalized Recommendations</h1>
          <p className="text-gray-400">Targeted action items based on your interview performance.</p>
        </div>
        <div className="flex items-center space-x-2">
          <Filter className="w-4 h-4 text-gray-500" />
          <select 
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            className="bg-[#161b22] border border-[#30363d] text-white text-sm rounded-md px-3 py-2 focus:outline-none focus:border-brand-500"
          >
            {categories.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>
      </div>

      {filteredRecs.length === 0 ? (
        <EmptyState icon={Lightbulb} title="No Recommendations" message="You're all caught up! Take another mock interview to get more insights." />
      ) : (
        <div className="grid gap-4">
          {filteredRecs.map(rec => (
            <div key={rec.id} className={`bg-[#161b22] border rounded-lg p-5 transition-colors ${rec.completed ? 'border-green-900/50 opacity-75' : 'border-[#30363d]'}`}>
              <div className="flex justify-between items-start mb-3">
                <div className="flex items-center space-x-3">
                  <Badge variant={getPriorityColor(rec.priority)} text={rec.priority} />
                  <span className="text-sm font-medium text-brand-400">{rec.category}</span>
                </div>
                <button 
                  onClick={() => toggleComplete(rec)}
                  disabled={savingIds.includes(rec.id)}
                  className={`flex items-center text-sm px-3 py-1.5 rounded-md transition-colors ${
                    rec.completed ? 'bg-green-900/20 text-green-500' : 'bg-[#30363d] text-gray-400 hover:text-white'
                  } disabled:opacity-50`}
                >
                  <CheckCircle2 className="w-4 h-4 mr-1.5" />
                  {savingIds.includes(rec.id) ? 'Saving...' : rec.completed ? 'Completed' : 'Mark Complete'}
                </button>
              </div>
              
              <h3 className={`text-lg font-semibold mb-2 ${rec.completed ? 'text-gray-400 line-through' : 'text-white'}`}>
                {rec.title}
              </h3>
              <p className="text-gray-400 text-sm mb-4">{rec.description}</p>
              
              {rec.resourceLinks && rec.resourceLinks.length > 0 && (
                <div className="flex flex-wrap gap-2 mt-4 pt-4 border-t border-[#30363d]">
                  <span className="flex items-center text-xs text-gray-500 mr-2">
                    <BookOpen className="w-3 h-3 mr-1" /> Resources:
                  </span>
                  {rec.resourceLinks.map((resource, idx) => (
                    <a key={idx} href={resource} target="_blank" rel="noopener noreferrer" className="flex items-center text-xs bg-[#0f1117] border border-[#30363d] hover:border-brand-500 text-brand-400 px-2 py-1 rounded transition-colors">
                      Resource {idx + 1} <ExternalLink className="w-3 h-3 ml-1" />
                    </a>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RecommendationsPage;
