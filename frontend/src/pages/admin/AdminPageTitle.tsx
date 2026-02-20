import type { ReactNode } from 'react';

type Props = {
  title: string;
  children?: ReactNode;
};

export default function AdminPageTitle({ title, children }: Props) {
  return (
    <section className="space-y-3">
      <h1 className="text-3xl font-black tracking-tight">Admin • {title}</h1>
      {children}
    </section>
  );
}
