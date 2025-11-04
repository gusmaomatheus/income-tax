import React from 'react';
import { Route, Routes } from 'react-router-dom';

import { DefaultLayout } from './components/layouts/DefaultLayout';
import { ProtectedLayout } from './components/layouts/ProtectedLayout';

import { DashboardPage } from './pages/DashboardPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';

function App(): React.JSX.Element {
  return (
    <Routes>
      <Route path="/" element={<DefaultLayout />}>
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Route>

      <Route path="/" element={<ProtectedLayout />}>
        <Route path="/dashboard" element={<DashboardPage />} />
      </Route>
    </Routes>
  );
}

export default App;