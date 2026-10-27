import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Code, MessageSquare, Layers, Clock, Loader2, ArrowRight } from 'lucide-react';
import { createInterview } from '../api/interviews';
import { INTERVIEW_TYPES, DIFFICULTIES, TOPICS } from '../utils/constants';
import toast from 'react-hot-toast';

const InterviewSetupPage = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    type: 'CODING',
    difficulty: 'EASY',
    topic: 'DATA_STRUCTURES'
  });

  const getDuration = () => {
    switch (formData.type) {
      case 'CODING': return 45;
      case 'TECHNICAL_QA': return 30;
      case 'MIXED': return 60;
      default: return 45;
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    try {
      const payload = {
        interviewType: formData.type,
        difficulty: formData.difficulty,
        topic: formData.topic,
        durationMinutes: getDuration()
      };
      const response = await createInterview(payload);
      
      toast.success('Interview setup complete');
      navigate(`/interviews/${response.id}/instructions`);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to create interview. Try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-white mb-2">Set Up Your Interview</h1>
        <p className="text-gray-400">Configure your mock interview session to target specific skills.</p>
      </div>

      <div className="space-y-8">
        {/* Type Selection */}
        <div>
          <h2 className="text-lg font-medium text-white mb-4">1. Select Interview Type</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {INTERVIEW_TYPES.map(type => (
              <div 
                key={type.id}
                onClick={() => setFormData({...formData, type: type.id})}
                className={`p-4 rounded-lg border cursor-pointer transition-all ${
                  formData.type === type.id 
                    ? 'bg-brand-900/20 border-brand-500' 
                    : 'bg-[#161b22] border-[#30363d] hover:border-[#484f58]'
                }`}
              >
                <div className={`w-10 h-10 rounded-full flex items-center justify-center mb-3 ${
                  formData.type === type.id ? 'bg-brand-600 text-white' : 'bg-[#30363d] text-gray-400'
                }`}>
                  {type.id === 'CODING' ? <Code className="w-5 h-5" /> : 
                   type.id === 'TECHNICAL_QA' ? <MessageSquare className="w-5 h-5" /> : 
                   <Layers className="w-5 h-5" />}
                </div>
                <h3 className={`font-semibold mb-1 ${formData.type === type.id ? 'text-white' : 'text-gray-300'}`}>
                  {type.name}
                </h3>
                <p className="text-sm text-gray-500">{type.description}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Topic Selection */}
        <div>
          <h2 className="text-lg font-medium text-white mb-4">2. Select Topic Focus</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
            {TOPICS.map(topic => (
              <button
                key={topic.id}
                onClick={() => setFormData({...formData, topic: topic.id})}
                className={`px-4 py-3 rounded-md border text-sm font-medium transition-colors ${
                  formData.topic === topic.id
                    ? 'bg-brand-900/20 border-brand-500 text-brand-400'
                    : 'bg-[#161b22] border-[#30363d] text-gray-400 hover:bg-[#30363d]'
                }`}
              >
                {topic.name}
              </button>
            ))}
          </div>
        </div>

        {/* Difficulty Selection */}
        <div>
          <h2 className="text-lg font-medium text-white mb-4">3. Select Difficulty Level</h2>
          <div className="flex space-x-4">
            {DIFFICULTIES.map(diff => {
              let colorClass = '';
              if (formData.difficulty === diff.id) {
                if (diff.id === 'EASY') colorClass = 'bg-green-900/20 border-green-500 text-green-400';
                else if (diff.id === 'MEDIUM') colorClass = 'bg-yellow-900/20 border-yellow-500 text-yellow-400';
                else if (diff.id === 'HARD') colorClass = 'bg-red-900/20 border-red-500 text-red-400';
              } else {
                colorClass = 'bg-[#161b22] border-[#30363d] text-gray-400 hover:bg-[#30363d]';
              }

              return (
                <button
                  key={diff.id}
                  onClick={() => setFormData({...formData, difficulty: diff.id})}
                  className={`flex-1 py-3 rounded-md border font-medium transition-colors ${colorClass}`}
                >
                  {diff.name}
                </button>
              );
            })}
          </div>
        </div>

        {/* Summary & Submit */}
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-6 flex flex-col sm:flex-row items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-white mb-1">Ready to start?</h3>
            <p className="text-sm text-gray-400 flex items-center">
              <Clock className="w-4 h-4 mr-1" /> Estimated duration: {getDuration()} minutes
            </p>
          </div>
          <button
            onClick={handleSubmit}
            disabled={isSubmitting}
            className="mt-4 sm:mt-0 px-6 py-3 bg-brand-600 hover:bg-brand-500 text-white font-medium rounded-md transition-colors flex items-center disabled:opacity-50"
          >
            {isSubmitting ? <Loader2 className="w-5 h-5 mr-2 animate-spin" /> : null}
            Continue to Instructions
            {!isSubmitting && <ArrowRight className="w-5 h-5 ml-2" />}
          </button>
        </div>
      </div>
    </div>
  );
};

export default InterviewSetupPage;
