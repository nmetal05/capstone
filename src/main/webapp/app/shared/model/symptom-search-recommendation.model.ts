import { ISymptomSearch } from 'app/shared/model/symptom-search.model';
import { ISpecialization } from 'app/shared/model/specialization.model';

export interface ISymptomSearchRecommendation {
  id?: number;
  confidenceScore?: number;
  rank?: number;
  reasoning?: string;
  search?: ISymptomSearch;
  specialization?: ISpecialization;
}

export const defaultValue: Readonly<ISymptomSearchRecommendation> = {};
