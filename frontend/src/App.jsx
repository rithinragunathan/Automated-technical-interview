import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import useAuth from './hooks/useAuth';

// Layouts
import AppLayout from './components/layout/AppLayout';
import AuthLayout from './components/layout/AuthLayout';

// Pages
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import ProfilePage from './pages/ProfilePage';
import InterviewHistoryPage from './pages/InterviewHistoryPage';
import InterviewSetupPage from './pages/InterviewSetupPage';
import InterviewInstructionsPage from './pages/InterviewInstructionsPage';
import LiveInterviewPage from './pages/LiveInterviewPage';
import InterviewSummaryPage from './pages/InterviewSummaryPage';
import ReportPage from './pages/ReportPage';
import ProblemsPage from './pages/ProblemsPage';
import CodingPage from './pages/CodingPage';
import AnalyticsPage from './pages/AnalyticsPage';
import RecommendationsPage from './pages/RecommendationsPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import ProblemManagementPage from './pages/ProblemManagementPage';

// Loading Component
import LoadingSpinner from './components/common/LoadingSpinner';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) {
    return <LoadingSpinner fullScreen message="Loading session..." />;
  }
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  return children;
};

const AdminRoute = ({ children }) => {
  const { isAuthenticated, isAdmin, loading } = useAuth();
  
  if (loading) {
    return <LoadingSpinner fullScreen message="Verifying admin..." />;
  }
  
  if (!isAuthenticated || !isAdmin) {
    return <Navigate to="/dashboard" replace />;
  }
  
  return children;
};

function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<LandingPage />} />
        
        {/* Auth Routes */}
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        {/* Protected Application Routes */}
        <Route element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/interviews" element={<InterviewHistoryPage />} />
          <Route path="/interviews/new" element={<InterviewSetupPage />} />
          <Route path="/interviews/:id/instructions" element={<InterviewInstructionsPage />} />
          <Route path="/interviews/:id/report" element={<ReportPage />} />
          <Route path="/problems" element={<ProblemsPage />} />
          <Route path="/analytics" element={<AnalyticsPage />} />
          <Route path="/recommendations" element={<RecommendationsPage />} />
          
          {/* Admin Routes */}
          <Route path="/admin" element={
            <AdminRoute>
              <AdminDashboardPage />
            </AdminRoute>
          } />
          <Route path="/admin/problems" element={
            <AdminRoute>
              <ProblemManagementPage />
            </AdminRoute>
          } />
        </Route>
        
        {/* Specialized Full-Screen Routes (no standard sidebar layout) */}
        <Route path="/interviews/:id/live" element={
          <ProtectedRoute>
            <LiveInterviewPage />
          </ProtectedRoute>
        } />
        <Route path="/interviews/:id/summary" element={
          <ProtectedRoute>
            <InterviewSummaryPage />
          </ProtectedRoute>
        } />
        <Route path="/problems/:id" element={
          <ProtectedRoute>
            <CodingPage />
          </ProtectedRoute>
        } />
        
        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
