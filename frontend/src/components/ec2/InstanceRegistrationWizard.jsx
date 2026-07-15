import React, { useMemo, useState } from 'react';

const AWS_REGIONS = [
  'us-east-1', 'us-east-2', 'us-west-1', 'us-west-2',
  'eu-west-1', 'eu-west-2', 'eu-central-1',
  'ap-south-1', 'ap-southeast-1', 'ap-southeast-2', 'ap-northeast-1'
];

const InstanceRegistrationWizard = ({ onComplete, onCancel }) => {
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [roleArn, setRoleArn] = useState('');
  const [acknowledged, setAcknowledged] = useState(false);
  const [formData, setFormData] = useState({
    awsAccountId: '',
    instanceId: '',
    region: 'us-east-1',
    nickname: '',
  });

  const rolePlaceholder = useMemo(
    () => `arn:aws:iam::${formData.awsAccountId || '123456789012'}:role/SentinalMonitorRole`,
    [formData.awsAccountId]
  );

  const accountRootArn = useMemo(
    () => `arn:aws:iam::${formData.awsAccountId || '123456789012'}:root`,
    [formData.awsAccountId]
  );

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
    setError(null);
  };

  const handleStepOne = (event) => {
    event.preventDefault();
    setError(null);
    setStep(2);
  };

  const handleStepTwo = (event) => {
    event.preventDefault();
    setError(null);

    if (!roleArn.trim()) {
      setError('Paste the IAM Role ARN before continuing.');
      return;
    }

    setStep(3);
  };

  const handleComplete = async () => {
    setLoading(true);
    setError(null);

    try {
      await onComplete({
        instanceId: formData.instanceId.trim(),
        region: formData.region,
        nickname: formData.nickname.trim(),
        roleArn: roleArn.trim(),
      });
    } catch (err) {
      setError(err.message || 'Failed to register instance');
      setLoading(false);
    }
  };

  const downloadCloudFormationTemplate = () => {
    const template = `AWSTemplateFormatVersion: '2010-09-09'

Parameters:
  ExternalId:
    Type: String
    Description: Unique ID provided by Sentinal

Resources:
  SentinalMonitorRole:
    Type: AWS::IAM::Role
    Properties:
      RoleName: SentinalMonitorRole
      AssumeRolePolicyDocument:
        Version: '2012-10-17'
        Statement:
          - Effect: Allow
            Principal:
              AWS: ${accountRootArn}
            Action: sts:AssumeRole
            Condition:
              StringEquals:
                sts:ExternalId: !Ref ExternalId
      Policies:
        - PolicyName: SentinalEC2ReadOnly
          PolicyDocument:
            Version: '2012-10-17'
            Statement:
              - Effect: Allow
                Action:
                  - ec2:DescribeInstances
                  - ec2:DescribeInstanceStatus
                  - ec2:RebootInstances
                  - ec2:StopInstances
                  - ec2:StartInstances
                Resource: "*"

Outputs:
  RoleArn:
    Description: "Copy this Role ARN and paste it into Sentinal"
    Value: !GetAtt SentinalMonitorRole.Arn
`;

    const blob = new Blob([template], { type: 'text/yaml' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'sentinal-monitor-role.yaml';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  return (
    <div className="space-y-6">
      <div className="border-b border-black/5 pb-5">
        <h3 className="text-lg font-semibold uppercase tracking-[0.18em] text-[#111827]">Register AWS EC2 Instance</h3>
        <div className="mt-4 grid gap-3 md:grid-cols-3">
          {[
            ['1. Instance Details', step >= 1],
            ['2. Role Or Stack Setup', step >= 2],
            ['3. Monitoring Install Guide', step >= 3],
          ].map(([label, active]) => (
            <div
              key={label}
              className={`rounded-2xl border px-4 py-3 text-xs font-semibold uppercase tracking-[0.18em] ${
                active
                  ? 'border-[#0f6b3d]/20 bg-[#eef7f0] text-[#0f6b3d]'
                  : 'border-black/10 bg-[#f7f8f4] text-[#7b817c]'
              }`}
            >
              {label}
            </div>
          ))}
        </div>
      </div>

      {error && (
        <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
          {error}
        </div>
      )}

      {step === 1 && (
        <form onSubmit={handleStepOne} className="space-y-5">
          <div className="rounded-[24px] border border-black/5 bg-[#f7f8f4] p-5">
            <h4 className="text-sm font-semibold uppercase tracking-[0.18em] text-[#111827]">Enter your EC2 instance details</h4>
            <p className="mt-3 text-sm leading-7 text-[#66736b]">
              Start with ownership and location details so Sentinal can generate the right IAM guidance.
            </p>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <WizardField
              id="awsAccountId"
              name="awsAccountId"
              label="Your AWS Account ID *"
              value={formData.awsAccountId}
              onChange={handleInputChange}
              placeholder="123456789012"
              pattern="\d{12}"
              helper="12-digit AWS account ID"
            />
            <WizardField
              id="instanceId"
              name="instanceId"
              label="Instance ID *"
              value={formData.instanceId}
              onChange={handleInputChange}
              placeholder="i-1234567890abcdef0"
              pattern="i-[a-f0-9]{8,17}"
              helper="Format: i-xxxxxxxxxxxxxxxxx"
            />
            <label className="block space-y-2">
              <span className="text-xs font-semibold uppercase tracking-[0.24em] text-[#66736b]">AWS Region *</span>
              <select
                id="region"
                name="region"
                value={formData.region}
                onChange={handleInputChange}
                className="w-full rounded-2xl border border-black/10 bg-[#f7f8f4] px-4 py-3 text-sm text-[#111827] outline-none transition-all duration-200 focus:border-[#0f6b3d]/35 focus:bg-white"
              >
                {AWS_REGIONS.map((region) => (
                  <option key={region} value={region}>{region}</option>
                ))}
              </select>
            </label>
            <WizardField
              id="nickname"
              name="nickname"
              label="Nickname *"
              value={formData.nickname}
              onChange={handleInputChange}
              placeholder="My Production Server"
              helper="Friendly name shown in Sentinal"
            />
          </div>

          <div className="flex items-center justify-between">
            <button type="button" onClick={onCancel} className="rounded-2xl border border-black/10 px-4 py-3 text-sm font-semibold uppercase tracking-[0.18em] text-[#66736b] transition-all duration-200 hover:bg-[#f7f8f4] hover:text-[#111827]">
              Cancel
            </button>
            <button type="submit" className="rounded-2xl bg-[#0f6b3d] px-5 py-3 text-sm font-semibold uppercase tracking-[0.2em] text-white shadow-[0_12px_28px_rgba(15,107,61,0.16)] transition-all duration-200 hover:-translate-y-0.5 hover:bg-[#0b5a33]">
              Next: Setup IAM Role
            </button>
          </div>
        </form>
      )}

      {step === 2 && (
        <div className="space-y-5">
          <div className="rounded-[24px] border border-black/5 bg-[#f7f8f4] p-5">
            <h4 className="text-sm font-semibold uppercase tracking-[0.18em] text-[#111827]">Setup IAM Role in AWS Console</h4>
            <p className="mt-3 text-sm leading-7 text-[#66736b]">
              Use the existing role/stack flow, then paste the Role ARN so Sentinal can register this instance.
            </p>
          </div>

          <div className="rounded-[24px] border border-[#0f6b3d]/15 bg-[#eef7f0] p-5">
            <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
              <div>
                <h5 className="text-sm font-semibold uppercase tracking-[0.18em] text-[#0f6b3d]">Step 1: Download CloudFormation Template</h5>
                <p className="mt-2 text-sm leading-7 text-[#66736b]">Template configured for AWS Account: {formData.awsAccountId}</p>
              </div>
              <button type="button" onClick={downloadCloudFormationTemplate} className="rounded-2xl bg-[#0f6b3d] px-5 py-3 text-sm font-semibold uppercase tracking-[0.18em] text-white transition-all duration-200 hover:-translate-y-0.5 hover:bg-[#0b5a33]">
                Download sentinal-monitor-role.yaml
              </button>
            </div>
          </div>

          <div className="rounded-[24px] border border-black/5 bg-[#f7f8f4] p-5">
            <h5 className="text-sm font-semibold uppercase tracking-[0.18em] text-[#111827]">Step 2: Create IAM Role Or Stack</h5>
            <ol className="mt-4 space-y-4 text-sm leading-7 text-[#66736b]">
              <li>Go to AWS CloudFormation and create a new stack using the downloaded `sentinal-monitor-role.yaml` template.</li>
              <li>Stack name: `SentinalMonitorStack`.</li>
              <li>Use a unique External ID when prompted.</li>
              <li>Acknowledge IAM resource creation and wait for `CREATE_COMPLETE`.</li>
              <li>Open the stack `Outputs` tab and copy the `RoleArn` value.</li>
            </ol>
            <div className="mt-5 rounded-2xl border border-black/10 bg-white px-4 py-3 text-sm text-[#111827]">
              Expected role ARN format: <code>{rolePlaceholder}</code>
            </div>
          </div>

          <form onSubmit={handleStepTwo} className="space-y-5">
            <WizardField
              id="roleArn"
              name="roleArn"
              label="IAM Role ARN *"
              value={roleArn}
              onChange={(event) => {
                setRoleArn(event.target.value);
                setError(null);
              }}
              placeholder={rolePlaceholder}
              pattern="arn:aws:iam::\d{12}:role\/.+"
              helper="Paste the Role ARN from CloudFormation outputs"
            />

            <div className="flex items-center justify-between">
              <button type="button" onClick={() => setStep(1)} className="rounded-2xl border border-black/10 px-4 py-3 text-sm font-semibold uppercase tracking-[0.18em] text-[#66736b] transition-all duration-200 hover:bg-[#f7f8f4] hover:text-[#111827]">
                Back
              </button>
              <button type="submit" className="rounded-2xl bg-[#0f6b3d] px-5 py-3 text-sm font-semibold uppercase tracking-[0.2em] text-white shadow-[0_12px_28px_rgba(15,107,61,0.16)] transition-all duration-200 hover:-translate-y-0.5 hover:bg-[#0b5a33]">
                Next: Install Monitoring
              </button>
            </div>
          </form>
        </div>
      )}

      {step === 3 && (
        <div className="space-y-5">
          <div className="rounded-[24px] border border-black/5 bg-[#f7f8f4] p-5">
            <h4 className="text-sm font-semibold uppercase tracking-[0.18em] text-[#111827]">Install Node Exporter, Prometheus, and Grafana</h4>
            <p className="mt-3 text-sm leading-7 text-[#66736b]">
              After role or stack permissions are ready, Sentinal now guides operators through the monitoring stack installation on the instance.
            </p>
          </div>

          <InstallCard
            title="Node Exporter"
            copy="Expose host CPU, memory, disk, and network metrics for Prometheus collection."
            command={`sudo useradd --no-create-home --shell /bin/false node_exporter
wget https://github.com/prometheus/node_exporter/releases/latest/download/node_exporter-*.linux-amd64.tar.gz
tar -xvf node_exporter-*.linux-amd64.tar.gz
sudo cp node_exporter-*.linux-amd64/node_exporter /usr/local/bin/
sudo tee /etc/systemd/system/node_exporter.service > /dev/null <<'EOF'
[Unit]
Description=Node Exporter
After=network.target

[Service]
User=node_exporter
Group=node_exporter
Type=simple
ExecStart=/usr/local/bin/node_exporter

[Install]
WantedBy=multi-user.target
EOF
sudo systemctl daemon-reload
sudo systemctl enable --now node_exporter`}
          />

          <InstallCard
            title="Prometheus"
            copy="Scrape Node Exporter from the instance and retain time-series metrics for Sentinal and Grafana."
            command={`wget https://github.com/prometheus/prometheus/releases/latest/download/prometheus-*.linux-amd64.tar.gz
tar -xvf prometheus-*.linux-amd64.tar.gz
cd prometheus-*.linux-amd64
cat <<'EOF' > prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'node'
    static_configs:
      - targets: ['localhost:9100']
EOF
./prometheus --config.file=prometheus.yml`}
          />

          <InstallCard
            title="Grafana"
            copy="Visualize Prometheus metrics through dashboards and embedded panels back in Sentinal."
            command={`sudo apt-get update
sudo apt-get install -y apt-transport-https software-properties-common wget
wget -q -O - https://packages.grafana.com/gpg.key | sudo apt-key add -
echo "deb https://packages.grafana.com/oss/deb stable main" | sudo tee /etc/apt/sources.list.d/grafana.list
sudo apt-get update
sudo apt-get install -y grafana
sudo systemctl enable --now grafana-server`}
          />

          <div className="rounded-[24px] border border-black/5 bg-[#f7f8f4] p-5">
            <h5 className="text-sm font-semibold uppercase tracking-[0.18em] text-[#111827]">Final Checks</h5>
            <ul className="mt-4 space-y-3 text-sm leading-7 text-[#66736b]">
              <li>Confirm Prometheus can scrape `localhost:9100` on the instance.</li>
              <li>Confirm Grafana is reachable and dashboard variables are configured for `var-instance`.</li>
              <li>Use the registered role ARN below when Sentinal polls and displays embedded Grafana panels.</li>
            </ul>
            <label className="mt-5 flex items-start gap-3 rounded-2xl border border-black/10 bg-white px-4 py-4">
              <input
                type="checkbox"
                checked={acknowledged}
                onChange={(event) => setAcknowledged(event.target.checked)}
                className="mt-1 h-4 w-4 accent-[#0f6b3d]"
              />
              <span className="text-sm leading-7 text-[#66736b]">
                I understand the monitoring installation steps and want to complete instance registration now.
              </span>
            </label>
          </div>

          <div className="flex items-center justify-between">
            <button type="button" onClick={() => setStep(2)} className="rounded-2xl border border-black/10 px-4 py-3 text-sm font-semibold uppercase tracking-[0.18em] text-[#66736b] transition-all duration-200 hover:bg-[#f7f8f4] hover:text-[#111827]">
              Back
            </button>
            <button
              type="button"
              onClick={handleComplete}
              disabled={!acknowledged || loading}
              className="rounded-2xl bg-[#0f6b3d] px-5 py-3 text-sm font-semibold uppercase tracking-[0.2em] text-white shadow-[0_12px_28px_rgba(15,107,61,0.16)] transition-all duration-200 hover:-translate-y-0.5 hover:bg-[#0b5a33] disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? 'Registering...' : 'Complete Registration'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

function WizardField({ id, name, label, value, onChange, placeholder, pattern, helper }) {
  return (
    <label className="block space-y-2">
      <span className="text-xs font-semibold uppercase tracking-[0.24em] text-[#66736b]">{label}</span>
      <input
        id={id}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        pattern={pattern}
        required
        className="w-full rounded-2xl border border-black/10 bg-[#f7f8f4] px-4 py-3 text-sm text-[#111827] outline-none transition-all duration-200 placeholder:text-[#9aa39d] focus:border-[#0f6b3d]/35 focus:bg-white"
      />
      {helper && <small className="text-xs text-[#66736b]">{helper}</small>}
    </label>
  );
}

function InstallCard({ title, copy, command }) {
  return (
    <div className="rounded-[24px] border border-black/5 bg-[#f7f8f4] p-5">
      <h5 className="text-sm font-semibold uppercase tracking-[0.18em] text-[#111827]">{title}</h5>
      <p className="mt-3 text-sm leading-7 text-[#66736b]">{copy}</p>
      <pre className="mt-4 overflow-x-auto rounded-2xl border border-black/10 bg-[#111827] p-4 text-xs leading-6 text-[#d1fae5]">
        <code>{command}</code>
      </pre>
    </div>
  );
}

export default InstanceRegistrationWizard;
