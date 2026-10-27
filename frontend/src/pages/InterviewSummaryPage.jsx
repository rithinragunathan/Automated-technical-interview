import React, { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Loader2 } from 'lucide-react';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { getReport } from '../api/interviews';
import toast from 'react-hot-toast';

const InterviewSummaryPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    let mounted = true;
    const fetchAndNavigate = async () => {
      try {
        await getReport(id); // Wakes up the generation or fetches it
        if (mounted) navigate(`/interviews/${id}/report`, { replace: true });
      } catch (err) {
        console.error(err);
        toast.error('Failed to generate report');
        if (mounted) navigate('/dashboard', { replace: true });
      }
    };
    fetchAndNavigate();
    return () => { mounted = false; };
  }, [id, navigate]);

  return (
    <div className="min-h-screen bg-[#0f1117] flex flex-col items-center justify-center text-center p-4">
      <div className="w-20 h-20 bg-brand-900/30 rounded-full flex items-center justify-center mb-6">
        <Loader2 className="w-10 h-10 text-brand-500 animate-spin" />
      </div>
      <h1 className="text-2xl font-bold text-white mb-2">Generating Your Report</h1>
      <p className="text-gray-400 max-w-md">
        We're evaluating your performance, analyzing code complexity, and generating personalized feedback. This will just take a moment...
      </p>
    </div>
  );
};

export default InterviewSummaryPage;
