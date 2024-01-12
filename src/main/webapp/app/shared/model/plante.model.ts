import { IPlantage } from 'app/shared/model/plantage.model';
import { ITypePlante } from 'app/shared/model/type-plante.model';
import { IParcelle } from 'app/shared/model/parcelle.model';

export interface IPlante {
  id?: number;
  libelle?: string | null;
  racine?: string | null;
  image?: string | null;
  plantages?: IPlantage[] | null;
  typePlante?: ITypePlante | null;
  nom?: ITypePlante | null;
  parcelles?: IParcelle[] | null;
}

export const defaultValue: Readonly<IPlante> = {};
