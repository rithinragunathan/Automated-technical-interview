import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2 } from 'lucide-react';
import Modal from '../components/common/Modal';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Badge from '../components/common/Badge';
import { getDifficultyColor } from '../utils/scoreHelpers';
import toast from 'react-hot-toast';

const ProblemManagementPage = () => {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState(null);

  useEffect(() => {
    // Mock fetch
    setTimeout(() => {
      setProblems([
        { id: '1', title: 'Two Sum', slug: 'two-sum', difficulty: 'EASY', topic: 'Arrays', testCasesCount: 5 }
      ]);
      setLoading(false);
    }, 500);
  }, []);

  const handleDelete = (id) => {
    // Mock delete
    setProblems(problems.filter(p => p.id !== id));
    toast.success('Problem deleted');
    setDeleteConfirm(null);
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-white">Problem Management</h1>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-md text-sm font-medium flex items-center"
        >
          <Plus className="w-4 h-4 mr-2" /> Add Problem
        </button>
      </div>

      <div className="bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden">
        <table className="min-w-full divide-y divide-[#30363d]">
          <thead className="bg-[#0f1117]">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Title</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Difficulty</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Topic</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#30363d]">
            {problems.map((problem) => (
              <tr key={problem.id} className="hover:bg-[#30363d]/50">
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-white">
                  {problem.title}
                  <div className="text-xs text-gray-500 font-normal">{problem.slug}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <Badge variant={getDifficultyColor(problem.difficulty)} text={problem.difficulty} />
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">
                  {problem.topic}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <button className="text-brand-400 hover:text-brand-300 mr-3">
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button 
                    className="text-red-400 hover:text-red-300"
                    onClick={() => setDeleteConfirm(problem.id)}
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Add New Problem" maxWidth="max-w-2xl">
        <div className="p-4 text-gray-400">
          Form to add problem goes here. Includes Monaco editor for starter code, dynamic test case fields, etc.
          <div className="mt-4 flex justify-end">
            <button onClick={() => setIsModalOpen(false)} className="px-4 py-2 bg-brand-600 text-white rounded">Close</button>
          </div>
        </div>
      </Modal>

      <ConfirmDialog
        isOpen={!!deleteConfirm}
        title="Delete Problem"
        message="Are you sure you want to delete this problem? This action cannot be undone."
        isDanger={true}
        onConfirm={() => handleDelete(deleteConfirm)}
        onCancel={() => setDeleteConfirm(null)}
      />
    </div>
  );
};

export default ProblemManagementPage;
