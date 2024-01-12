import { IPlante } from 'app/shared/model/plante.model';

export interface ITypePlante {
  id?: number;
  name?: string | null;
  humiditeMax?: number | null;
  humiditeMin?: number | null;
  temperature?: number | null;
  plantes?: IPlante[] | null;
}

export const defaultValue: Readonly<ITypePlante> = {};
