import React, { useState } from 'react';
import { Alert, Button, Form } from 'reactstrap';
import axios from 'axios';
import dayjs from 'dayjs';
import './styles/symptom-analysis.scss';

interface Suggestion {
  specialization: string;
  confidence: number;
  reason: string;
}

interface ApiResponse {
  suggestions: Suggestion[];
}

const SymptomAnalysis: React.FC = () => {
  const [symptoms, setSymptoms] = useState('');
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const saveSymptomSearch = async (symptomText: string, aiResponse: ApiResponse) => {
    try {
      // Get current user's profile - try the current endpoint first
      let currentUserProfile = null;
      try {
        const currentProfileResponse = await axios.get('/api/app-user-profiles/current');
        currentUserProfile = currentProfileResponse.data;
      } catch (err) {
        console.log('No current user profile found, user might not have an AppUserProfile yet');
        // For now, we'll skip saving if user doesn't have a profile
        // In the future, we could create one automatically or save to guest session
        return;
      }

      if (!currentUserProfile) {
        console.log('No user profile available, skipping symptom search save');
        return;
      }

      // Save the symptom search
      const symptomSearchData = {
        searchDate: dayjs().toISOString(),
        symptoms: symptomText,
        aiResponseJson: JSON.stringify(aiResponse),
        user: { id: currentUserProfile.id },
        guestSession: null,
      };

      const searchResponse = await axios.post('/api/symptom-searches', symptomSearchData);
      const searchId = searchResponse.data.id;

      // Then, save the recommendations
      if (aiResponse.suggestions && aiResponse.suggestions.length > 0) {
        // First, get all specializations to map names to IDs
        const specializationsResponse = await axios.get('/api/specializations?size=1000');
        const specializations = specializationsResponse.data;

        for (let i = 0; i < aiResponse.suggestions.length; i++) {
          const suggestion = aiResponse.suggestions[i];

          // Find the specialization by name
          const specialization = specializations.find((spec: any) => spec.name.toLowerCase() === suggestion.specialization.toLowerCase());

          if (specialization) {
            const recommendationData = {
              confidenceScore: suggestion.confidence,
              rank: i + 1,
              reasoning: suggestion.reason,
              search: { id: searchId },
              specialization: { id: specialization.id },
            };

            await axios.post('/api/symptom-search-recommendations', recommendationData);
          }
        }
      }

      console.log('Symptom search and recommendations saved successfully');
    } catch (err) {
      console.error('Failed to save symptom search history:', err);
      // Don't show error to user as this is background functionality
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!symptoms.trim()) {
      setError('Please describe your symptoms');
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const response = await axios.post<ApiResponse>('/api/ai/symptom-to-spec', {
        symptoms,
      });
      setSuggestions(response.data.suggestions);

      // Save to history in the background
      await saveSymptomSearch(symptoms, response.data);
    } catch (err) {
      setError('Failed to analyze symptoms. Please try again later.');
      console.error('API Error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const getConfidenceColor = (confidence: number) => {
    if (confidence >= 0.8) return 'success';
    if (confidence >= 0.6) return 'info';
    if (confidence >= 0.4) return 'warning';
    return 'danger';
  };

  return (
    <div className="card shadow-sm h-100">
      <div className="card-body">
        <h3 className="card-title mb-4">
          <i className="fas fa-stethoscope me-2"></i>
          Symptom Analysis
        </h3>
        <p className="text-muted mb-4">
          Describe your symptoms and we&apos;ll suggest the most appropriate medical specialists for your condition.
        </p>

        <Form onSubmit={handleSubmit}>
          <div className="mb-3">
            <textarea
              className="form-control"
              rows={5}
              placeholder="Describe your symptoms in detail (e.g., 'I've been experiencing dizziness and headaches for the past week')"
              value={symptoms}
              onChange={e => setSymptoms(e.target.value)}
              disabled={isLoading}
            />
          </div>

          {error && <Alert color="danger">{error}</Alert>}

          <Button color="primary" type="submit" disabled={isLoading} className="w-100 py-2">
            {isLoading ? (
              <>
                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                Analyzing...
              </>
            ) : (
              <>
                <i className="fas fa-search me-2"></i>
                Analyze Symptoms
              </>
            )}
          </Button>
        </Form>

        {suggestions.length > 0 && (
          <div className="mt-4">
            <h5 className="mb-3">Recommended Specialists:</h5>
            <div className="suggestions-grid">
              {suggestions.map((suggestion, index) => (
                <div key={index} className="suggestion-card">
                  <Alert color={getConfidenceColor(suggestion.confidence)} className="rounded-lg">
                    <div className="d-flex justify-content-between align-items-start">
                      <h5 className="mb-2">{suggestion.specialization}</h5>
                      <span className="badge bg-white text-dark">{Math.round(suggestion.confidence * 100)}% confidence</span>
                    </div>
                    <p className="mb-0">{suggestion.reason}</p>
                  </Alert>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SymptomAnalysis;
