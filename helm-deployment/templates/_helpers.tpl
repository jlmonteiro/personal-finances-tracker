{{- define "finances.fullname" -}}
{{- printf "%s" .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "finances.backend.fullname" -}}
{{- printf "%s-backend" .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "finances.frontend.fullname" -}}
{{- printf "%s-frontend" .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "finances.labels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "finances.backend.labels" -}}
{{ include "finances.labels" . }}
app.kubernetes.io/name: {{ .Chart.Name }}-backend
app.kubernetes.io/component: backend
{{- end }}

{{- define "finances.backend.selectorLabels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}-backend
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "finances.frontend.labels" -}}
{{ include "finances.labels" . }}
app.kubernetes.io/name: {{ .Chart.Name }}-frontend
app.kubernetes.io/component: frontend
{{- end }}

{{- define "finances.frontend.selectorLabels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}-frontend
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "finances.backend.serviceAccountName" -}}
{{- if .Values.backend.serviceAccount.name }}
{{- .Values.backend.serviceAccount.name }}
{{- else }}
{{- include "finances.backend.fullname" . }}
{{- end }}
{{- end }}

{{- define "finances.frontend.serviceAccountName" -}}
{{- if .Values.frontend.serviceAccount.name }}
{{- .Values.frontend.serviceAccount.name }}
{{- else }}
{{- include "finances.frontend.fullname" . }}
{{- end }}
{{- end }}
