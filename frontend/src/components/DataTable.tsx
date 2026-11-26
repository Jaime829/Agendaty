import { ReactNode, useMemo, useState } from 'react';
import { EmptyState } from '@/components/EmptyState';
import { Button } from '@/components/Button';

export interface DataTableColumn<T> {
  key: string;
  header: string;
  render?: (row: T) => ReactNode;
  sortValue?: (row: T) => string | number;
  align?: 'left' | 'right';
  className?: string;
}

interface DataTableProps<T> {
  columns: DataTableColumn<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  searchText?: (row: T) => string;
  searchPlaceholder?: string;
  filterValue?: (row: T) => string;
  filterOptions?: { value: string; label: string }[];
  filterLabel?: string;
  pageSize?: number;
  caption?: string;
}

export function DataTable<T>({
  columns,
  rows,
  rowKey,
  searchText,
  searchPlaceholder = 'Buscar…',
  filterValue,
  filterOptions,
  filterLabel = 'Filtrar',
  pageSize = 10,
  caption,
}: DataTableProps<T>) {
  const [busqueda, setBusqueda] = useState('');
  const [filtro, setFiltro] = useState('');
  const [orden, setOrden] = useState<{ key: string; dir: 1 | -1 } | null>(null);
  const [pagina, setPagina] = useState(1);

  const filtradas = useMemo(() => {
    let base = rows;
    if (searchText && busqueda.trim()) {
      const q = busqueda.trim().toLowerCase();
      base = base.filter((r) => searchText(r).toLowerCase().includes(q));
    }
    if (filterValue && filtro) {
      base = base.filter((r) => filterValue(r) === filtro);
    }
    if (orden) {
      const col = columns.find((c) => c.key === orden.key);
      if (col?.sortValue) {
        base = [...base].sort((a, b) => {
          const va = col.sortValue!(a);
          const vb = col.sortValue!(b);
          if (va < vb) return -1 * orden.dir;
          if (va > vb) return 1 * orden.dir;
          return 0;
        });
      }
    }
    return base;
  }, [rows, busqueda, filtro, orden, columns, searchText, filterValue]);

  const totalPaginas = Math.max(1, Math.ceil(filtradas.length / pageSize));
  const paginaSegura = Math.min(pagina, totalPaginas);
  const inicio = (paginaSegura - 1) * pageSize;
  const visibles = filtradas.slice(inicio, inicio + pageSize);

  const alternarOrden = (key: string) => {
    setOrden((prev) => {
      if (!prev || prev.key !== key) return { key, dir: 1 };
      if (prev.dir === 1) return { key, dir: -1 };
      return null;
    });
  };

  return (
    <div>
      {(searchText || filterOptions) && (
        <div className="mb-3 flex flex-col gap-2 sm:flex-row sm:items-center">
          {searchText && (
            <label className="relative block flex-1">
              <span className="sr-only">{searchPlaceholder}</span>
              <svg className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
                <circle cx="11" cy="11" r="7" />
                <path d="m21 21-4-4" />
              </svg>
              <input
                type="search"
                value={busqueda}
                onChange={(e) => {
                  setBusqueda(e.target.value);
                  setPagina(1);
                }}
                placeholder={searchPlaceholder}
                className="h-10 w-full rounded-md border border-slate-300 pl-9 pr-3 text-sm placeholder-slate-400 focus:border-brand-600 focus:outline-none focus:ring-2 focus:ring-brand-600"
              />
            </label>
          )}
          {filterOptions && (
            <label className="flex items-center gap-2 text-sm text-slate-600">
              <span className="whitespace-nowrap font-medium">{filterLabel}</span>
              <select
                value={filtro}
                onChange={(e) => {
                  setFiltro(e.target.value);
                  setPagina(1);
                }}
                className="h-10 rounded-md border border-slate-300 bg-white px-3 text-sm focus:border-brand-600 focus:outline-none focus:ring-2 focus:ring-brand-600"
              >
                <option value="">Todos</option>
                {filterOptions.map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </select>
            </label>
          )}
        </div>
      )}

      {filtradas.length === 0 ? (
        <EmptyState
          titulo={busqueda || filtro ? 'Nada coincide con tu búsqueda' : 'Aún no hay registros'}
          descripcion={busqueda || filtro ? 'Prueba con otros términos o filtros.' : 'Cuando se creen, aparecerán aquí.'}
        />
      ) : (
        <>
          <div className="overflow-x-auto">
            <table className="table-data">
              {caption && <caption className="sr-only">{caption}</caption>}
              <thead>
                <tr>
                  {columns.map((c) => (
                    <th key={c.key} scope="col" className={c.align === 'right' ? 'text-right' : ''}>
                      {c.sortValue ? (
                        <button
                          type="button"
                          onClick={() => alternarOrden(c.key)}
                          className="inline-flex items-center gap-1 rounded text-inherit hover:text-slate-800 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-600"
                          aria-sort={orden?.key === c.key ? (orden.dir === 1 ? 'ascending' : 'descending') : 'none'}
                        >
                          {c.header}
                          <span aria-hidden="true" className="text-[10px] text-slate-400">
                            {orden?.key === c.key ? (orden.dir === 1 ? '▲' : '▼') : '↕'}
                          </span>
                        </button>
                      ) : (
                        c.header
                      )}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {visibles.map((row) => (
                  <tr key={rowKey(row)}>
                    {columns.map((c) => (
                      <td key={c.key} className={c.align === 'right' ? 'text-right' : ''}>
                        {c.render ? c.render(row) : String((row as any)[c.key] ?? '')}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="mt-3 flex flex-wrap items-center justify-between gap-2 border-t border-slate-100 pt-3 text-xs text-slate-500">
            <p>
              Mostrando <span className="font-semibold text-slate-700">{inicio + 1}–{Math.min(inicio + pageSize, filtradas.length)}</span> de{' '}
              <span className="font-semibold text-slate-700">{filtradas.length}</span>
            </p>
            <div className="flex items-center gap-2">
              <Button variant="ghost" size="sm" onClick={() => setPagina((p) => Math.max(1, p - 1))} disabled={paginaSegura === 1}>
                Anterior
              </Button>
              <span className="tabular-nums">
                Página {paginaSegura} de {totalPaginas}
              </span>
              <Button variant="ghost" size="sm" onClick={() => setPagina((p) => Math.min(totalPaginas, p + 1))} disabled={paginaSegura === totalPaginas}>
                Siguiente
              </Button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
